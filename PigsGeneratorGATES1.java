/*
   Copyright 2011 by Francesco Pizzitutti
   Licensed under the Academic Free License version 3.0
   See the file "LICENSE" for more information
   */

package sim.app.cystiagents;

import sim.engine.*;
import sim.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.*; 

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory; // This is included in poi-ooxml-3.6-20091214.jar
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Calendar;
import java.util.Locale;
import static java.util.Calendar.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;  

import com.bbn.openmap.proj.coords.UTMPoint;
import com.bbn.openmap.proj.coords.LatLonPoint;
import com.vividsolutions.jts.geom.Point;

import sim.util.geo.MasonGeometry;

public class PigsGeneratorGATES1 implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public boolean printOut = false;

    public List<List<String>> houseLines = new ArrayList<List<String>>();
    public HashMap<Integer, List<List<String>>> housesLinesMap = new HashMap<Integer, List<List<String>>>();

    public List<List<String>> pigsLines = new ArrayList<List<String>>();

    public List<Integer> pigsWeights = new ArrayList<Integer>();
    public List<Integer> pigsTongues = new ArrayList<Integer>();
    public List<Integer> pigsNBands = new ArrayList<Integer>();
    public List<Integer> pigsGradeOfInfection = new ArrayList<Integer>();
    public List<Integer> pigsNViableCysts = new ArrayList<Integer>();
    public List<Integer> pigsNDegeneratedCysts = new ArrayList<Integer>();
    public List<Integer> pigsNViableCystsVillage = new ArrayList<Integer>();
    public List<Integer> pigsNDegeneratedCystsVillage = new ArrayList<Integer>();
    public List<Integer> pigsNCysts = new ArrayList<Integer>();

    public SortedSet<Integer> keySet = null;

    public HSSFWorkbook workbookHistoAll = new HSSFWorkbook();
    public HSSFWorkbook workbookHistoVillage = new HSSFWorkbook();

    public int totNumTreatedPigsAllVillages = 0;
    public HashMap<Integer, Boolean> treatedPigsAllVillages = new HashMap <Integer, Boolean>();

    public HashMap<Integer, Boolean> seropositivePigsAllVillages = new HashMap <Integer, Boolean>();
    public int numSeropositivePigsAllVillages = 0;

    public int numSeropositivePigsVillage = 0;
    public int numNecroVilla = 0;
    public int numNecroAll = 0;

    public int GATES2Round = 0;

    //====================================================
    public PigsGeneratorGATES1(final SimState pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;
    }

    //====================================================
    public void step(final SimState state)
    {

    }

    //Generate starting pigs population ===============
    public void generatePigs()
    {
        int stats = 0;
        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);
            if(!hh.pigOwner)continue;

            double random = state.random.nextDouble();
            random = - sim.pigsPerHousehold * Math.log(random);

            //if(sim.extendedOutput)System.out.println("Exp par, number " + sim.pigsPerHousehold + " " + random);

            for(int j = 0; j < random; j++)
            {
                stats++;
                Pig pig = new Pig(state, hh, false, false);

                double rand = state.random.nextDouble();
                if(rand < sim.importPrev)
                {
                    sim.pigsGen.assignPigCysts(pig);
                }

                pig.age = (int)Math.round(pig.slaughterAge * (1 - state.random.nextDouble()));

                //Here possible IanError he infect lightly only the pigs that are not 
                //heavily infected. it should be the proportion of the total number of 
                //pigs and not of the pig not heavily infected. Simulation changes?
            }
        }
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + stats + " Pigs generated");
        //System.exit(0);
    }

    //============================================================   
    //count the total number of defecation sites in the home range of each pig
    public void countNumberDefecationSitesAroundPigs()
    {
        for(int i = 1; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);
            pig.countDefecSites(pig);
        }
    }

    //============================================================   
    public void readPigsGATES1()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName  + " --------------------------------------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ---- Reading Pigs GATES1 --------");
        if(sim.extendedOutput)System.out.println(" ");

        getHousesLinesFromPigsFileGATES1();

        List<String> line = houseLines.get(0);

        int stats = 0;
        int statsPigs = 0;
        int statsPigsHh = 0;

        int statsPigsInfected = 0;
        int statsPigsInfectedNorm = 0;

        int statsPigsSeropositive = 0;
        int statsPigsSeropositiveNorm = 0;

        double perseroPrevalencePiglets = 0.0;
        double perseroPrevalencePigletsNorm = 0.0;
        double perseroPrevalenceTransition = 0.0;
        double perseroPrevalenceTransitionNorm = 0.0;
        double perseroPrevalenceYoungPigs = 0.0;
        double perseroPrevalenceYoungPigsNorm = 0.0;
        double perseroPrevalenceAdultPigs = 0.0;
        double perseroPrevalenceAdultPigsNorm = 0.0;

        int ageM = 0;

        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);
            //if(!hh.pigOwner)continue;

            statsPigsHh = 0;
            //System.out.println ("Househols shpId number: " + hh.shpId);

            houseLines = housesLinesMap.get(hh.shpId);

            if(houseLines == null)continue;

            hh.pigOwner = true;

            //if(houseLines != null)hh.pigOwner = true;
            //else continue;

            //System.out.println ("Househols shpId number: " + hh.shpId);
            //System.out.println (houseLines.size());

            for(int f = 0; f < houseLines.size(); f++)
            {
                line = (List<String>)houseLines.get(f);
                //System.out.println (line);
                //System.out.println (line.get(3));

                //pig is initialized but not infected


                //read serological data ----------------
                int numBands = -100;
                if(line.size() > 7)
                {
                    numBands = 0;
                    //this is the GP50 band: removed for cross-reaction
                    // numBands = numBands + Integer.parseInt(line.get(8));

                    numBands = numBands + Integer.parseInt(line.get(9));
                    numBands = numBands + Integer.parseInt(line.get(10));
                    numBands = numBands + Integer.parseInt(line.get(11));
                    numBands = numBands + Integer.parseInt(line.get(12));
                    numBands = numBands + Integer.parseInt(line.get(13));
                    numBands = numBands + Integer.parseInt(line.get(14));
                    //if(line.size() > 19)numBands = numBands + Integer.parseInt(line.get(19));

                    statsPigsSeropositiveNorm++;

                    if(numBands > 0)
                    {
                        statsPigsSeropositive++;
                    }
                }

                //read necroscopy data ----------------
                int numCysts = -100;
                if(!line.get(16).equals(""))
                {
                    numCysts = 0;
                    statsPigsInfectedNorm++;

                    //System.out.println (line.get(17));

                    numCysts = numCysts + Integer.parseInt(line.get(17));

                    if(numCysts > 0)statsPigsInfected++;
                }

                Pig pig = new Pig(state, hh, false, false);

                if(numBands > 0)
                {
                    pig.seropositive = true;
                    pig.seropositiveForExposure = true;
                }

                double rand = state.random.nextDouble();
                //baseline pig PC is 5.5%
                if(rand < 0.055)
                {
                    sim.pigsGen.assignPigCysts(pig);
                }

                pig.GATESID = Integer.parseInt(line.get(4));

                pig.age = (int)Math.round(sim.weeksInAYear * ( Integer.parseInt(line.get(6)))  );
               

                if(printOut)if(sim.extendedOutput)System.out.println ("New Pig age: " + (pig.age));

                ageM = (int)Math.round(( Integer.parseInt(line.get(6)))  );

                if(ageM >= 2 && ageM <= 3)
                {
                    perseroPrevalencePigletsNorm++;
                    if(numBands > 0)perseroPrevalencePiglets++;
                }

                if(ageM == 4)
                {
                    perseroPrevalenceTransitionNorm++;
                    if(numBands > 0)perseroPrevalenceTransition++;
                }

                if(ageM >= 5 && ageM <= 9)
                {
                    perseroPrevalenceYoungPigsNorm++;
                    if(numBands > 0)perseroPrevalenceYoungPigs++;
                }

                if(ageM >= 10)
                {
                    perseroPrevalenceAdultPigsNorm++;
                    if(numBands > 0)perseroPrevalenceAdultPigs++;
                }



                //gender
                if(line.get(5).equals("0"))pig.gender = "female";
                else pig.gender = "male";
                if(printOut)if(sim.extendedOutput)System.out.println ("New Pig gender: " + pig.gender);

                //if(!line.get(11).equals(sim.villageNameNumber))pig.imported = true;

                if(line.size() <= 10)continue;

                if(numBands > 0)pig.seropositive = true;
                else if(numBands == 0)pig.seropositive = false;
                else pig.seropositive = null;

                statsPigs++;
                statsPigsHh++;

                //data on corral use for GATES1 not available to F Pizzitutti 2021 
                //no use is set fro all the households
                pig.corraled = "never";
                //if(hh.corralUse.equals("never"))pig.corraled = "never";
                //else if(hh.corralUse.equals("sometimes"))pig.corraled = "sometimes";
                //else if(hh.corralUse.equals("always"))pig.corraled = "always";


                //System.out.println (numBands + " " + GATES2Round);

                Boolean ex = false;
                for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
                {
                    CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);
                    if(cp.ID == Integer.parseInt(line.get(1)))
                    {
                        if(numBands > 0)cp.roundsSeroState.set(1, 1);
                        else if(numBands == 0)cp.roundsSeroState.set(1, 0);

                        ex = true;
                        break;
                    }
                }

                if(!ex)
                {
                    CensedPig cp = new CensedPig(state, pig.GATESID);
                    if(sim.simW.studyVillages)sim.simW.censedPigs.add(cp);
                    if(numBands > 0)cp.roundsSeroState.set(1, 1);
                    else if(numBands == 0)cp.roundsSeroState.set(1, 0);
                }

                //System.out.println("pig: " + pig.GATESID + " num bands: " + numBands); 

            }

            if(GATES2Round == 1)hh.targetNumOfPigs = statsPigsHh;
            //System.out.println(hh.targetNumOfPigs);

        }


        if(sim.extendedOutput)System.out.println(sim.villageName + " tot num pigs generated: " + statsPigs);
        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " tot num pigs necropsied: " + statsPigsInfectedNorm);
        if(sim.extendedOutput)System.out.println(sim.villageName + " tot num pigs necropsied infected: " + statsPigsInfected);
        if(sim.extendedOutput)System.out.println(sim.villageName + " PC prevalence: " + (double)statsPigsInfected/(double)statsPigsInfectedNorm);
        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " tot num pigs seropositive: " + statsPigsSeropositive);
        if(sim.extendedOutput)System.out.println(sim.villageName + " pigs seroprevalence: " + (double)statsPigsSeropositive/(double)statsPigsSeropositiveNorm);

        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + "---- Prevalence by pigs age segments:");
        String tmp = " Avg. observed Piglets Seroprevalence with the seroincidence cohorts periodicity (7 <= age <= 14 weeks): ";
        if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  perseroPrevalencePiglets/(double)perseroPrevalencePigletsNorm);

        tmp = " Avg. observed Pigs Seroprevalence in the transition age segment with the seroincidence cohorts periodicity (15 <= age <= 20): ";
        if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  perseroPrevalenceTransition/(double)perseroPrevalenceTransitionNorm);

        tmp = " Avg. observed Young Pigs Seroprevalence with the seroincidence cohorts periodicity (21 <= age <= 37 weeks): ";
        if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  perseroPrevalenceYoungPigs/(double)perseroPrevalenceYoungPigsNorm);

        tmp = " Avg. observed Adult Pigs Seroprevalence with the seroincidence cohorts periodicity (38 <= age weeks): ";
        if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  perseroPrevalenceAdultPigs/(double)perseroPrevalenceAdultPigsNorm);
        if(sim.extendedOutput)System.out.println(" ");



        //System.exit(0);


    }

    public void getHousesLinesFromPigsFileGATES1()
    {
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/populationsData/GATES1/Gates1_Saca1_2005.xlsx";
        sheetName = "Gates 1 Saca 1 2005";
        if(sim.extendedOutput)System.out.println ("Survey input file: " + inputFile);
        //System.exit(0);

        try{
            Workbook workbookFile = WorkbookFactory.create(new FileInputStream(inputFile));

            Sheet sheet = workbookFile.getSheet(sheetName);
            //System.out.println (sheet);
            //System.exit(0);

            int statsRows = -1;
            int statsCells = -1;
            int lastCellNum = 0;
            int p = 0;
            int m = 0;

            boolean startRead = false;
            boolean stopRead = false;
            houseLines = new ArrayList<List<String>>();
            List<String> line = new ArrayList<String>();
rows:             
            for(Row row : sheet)
            { 
                statsRows++;
                //if(statsRows == 0)continue;
                //System.out.println ("nrow: " + statsRows);

                int stats = -1;
                Boolean read = false;

                line = new ArrayList<String>();

                for (int i = 0; i < row.getLastCellNum(); i++) 
                {  
                    Cell cell = row.getCell(i);
                    statsCells++;

                    //System.out.println (cell);

                    String stri = "";

                    if(cell == null)
                    {
                        stri = "";
                        line.add(stri);
                        continue;
                    }

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            stri = "";
                            //if(sim.extendedOutput)System.out.println ("dsadadsasd");
                        case Cell.CELL_TYPE_STRING:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");  
                                stri = dateFormat.format(date);  
                            }
                            else
                            {
                                double d = (double)cell.getNumericCellValue();
                                int aaa = (int)Math.round(d);
                                stri = Integer.toString(aaa);
                            }
                            break;
                        default:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                    }
                    line.add(stri);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                if(line.get(1).equals(sim.villageNameNumber))
                {
                    //if(sim.extendedOutput)System.out.println (line);
                    //System.exit(0);

                    int houseNum = Integer.parseInt(line.get(3));

                    if(housesLinesMap.containsKey(houseNum))
                    {
                        houseLines = housesLinesMap.get(houseNum);
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }
                    else
                    {
                        houseLines = new ArrayList<List<String>>();
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }

                }

            }

        }
        catch(FileNotFoundException e)
        {
            System.out.println(e);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        catch(InvalidFormatException e)
        {
            System.out.println(e);
        }



    }


    //============================================================   
    public Pig getPig(int cId)
    {
        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);
            if(p.censusIdentity == cId)return p;
        }
        return null;
    }

    //===============================================
    public SeroPig getSeroPigById(int id)
    {
        for(SeroPig sPig : sim.seroPigList)
        {
            if(sPig.pigId == id)return sPig;
        }
        return null;
    }

    //===============================================
    public void writeSerology()
    {
        if(sim.extendedOutput)System.out.println("================================================");
        if(sim.extendedOutput)System.out.println ("Writing  to spatial analysis (satscan) out file..........");
        if(sim.extendedOutput)System.out.println(" ");

        String dirSero = sim.outDir + "/serologySatscan/";

        File theDir = new File(dirSero);
        if (!theDir.exists())
        {
            if(sim.extendedOutput)System.out.println("creating directory: " + dirSero);
            theDir.mkdir();
        }


        for(int round = 0; round < 4; round++)
        {
            String fileCases = dirSero + "cases"  + (round + 1)  + ".cas";
            File fC = new File(fileCases);
            FileWriter fileWriterCases = null;

            String filePop = dirSero + "pop"  + (round + 1)  + ".pop";
            File fP = new File(filePop);
            FileWriter fileWriterPop = null;

            String fileCor = dirSero + "cor"  + (round + 1)  + ".cor";
            File fCor = new File(fileCor);
            FileWriter fileWriterCor = null;

            int len = 0;
            String line = "";
            int cases = 0;
            int pop = 0;
            String hFile = "";
            int idi = 0;

            //case file -----------------
            try{
                fileWriterCases = new FileWriter(fC);
                fileWriterPop = new FileWriter(fP);
                fileWriterCor = new FileWriter(fCor);

                for(int i = 0; i < sim.householdsBag.size(); i ++)
                {
                    Household hh = (Household)sim.householdsBag.get(i);
                    MasonGeometry mgHome = (MasonGeometry)hh.mGeometry;

                    line = "";

                    //cases
                    cases = hh.seropositivePigs.get(round);
                    idi = hh.shpId;
                    line = idi + " " + cases + " \n";
                    fileWriterCases.write(line);


                    line = "";

                    //pop
                    pop = hh.numPigsRound.get(round);
                    idi = hh.shpId;
                    line = idi + " " + round + " " + pop  +  " \n";
                    fileWriterPop.write(line);

                    //coordinates

                    line = "";

                    line = hh.shpId + " " + mgHome.getDoubleAttribute("latitude") + " "  + mgHome.getDoubleAttribute("longitude")  +  " \n";


                    fileWriterCor.write(line);

                    //System.out.println (line);

                }

                // force bytes to the underlying stream
                fileWriterCases.close();
                fileWriterPop.close();
                fileWriterCor.close();

            } catch (IOException ex) {
                System.out.println(ex);

            }//End filewriters


        }//end of main stage loop


        //System.exit(0);


    }




    //============================================================   
}
