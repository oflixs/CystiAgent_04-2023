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

public class PigsGeneratorGATES2 implements Steppable
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

    public List<Double> prevalenceByAge = new ArrayList<Double>();
    public List<Double> prevalenceByAgeNorm = new ArrayList<Double>();

    //====================================================
    public PigsGeneratorGATES2(final SimState pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;
    }

    //====================================================
    public void step(final SimState state)
    {

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
    public void readPorcinoGATES2Viajes()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ==================================================");
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ---- Initializing pigs from survey data GATES2 --------");
        if(sim.extendedOutput)System.out.println(" ");


        GATES2Round = 1;//this is the round 1 of mass treatment
        readViajeGATES2("WBviaje8");
        //System.exit(0);
        GATES2Round = 2;//this is the round 2 of mass treatment
        readViajeGATES2("WBviaje9");
        GATES2Round = 3;//this is the round 3 of mass treatment
        readViajeGATES2("WBviaje10");
        GATES2Round = 4;//this is the round 4 of mass treatment
        readViajeGATES2("WBviaje11");
        GATES2Round = 5;//this is the round 5  of mass treatment
        readViajeGATES2("WBviaje12");
        //System.exit(0);

        int numPigsRound1 = 0;
        int numPigsRound2 = 0;
        int numPigsRound3 = 0;
        int numPigsRound4 = 0;
        int numPigsRound5 = 0;
        int numPigsRound6 = 0;

        int numSeroPigsRound1 = 0;
        int numSeroPigsRound2 = 0;
        int numSeroPigsRound3 = 0;
        int numSeroPigsRound4 = 0;
        int numSeroPigsRound5 = 0;
        int numSeroPigsRound6 = 0;

        System.out.println("Num censedPigs: " + sim.censedPigsBag.size());

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);
            //System.out.println(cp.roundsSeroState.get(1));

            if(cp.roundsSeroState.get(1) == 1)numSeroPigsRound1++;
            if(cp.roundsSeroState.get(1) == 1 || cp.roundsSeroState.get(1) == 0)numPigsRound1++;

            if(cp.roundsSeroState.get(2) == 1)numSeroPigsRound2++;
            if(cp.roundsSeroState.get(2) == 1 || cp.roundsSeroState.get(2) == 0)numPigsRound2++;

            if(cp.roundsSeroState.get(3) == 1)numSeroPigsRound3++;
            if(cp.roundsSeroState.get(3) == 1 || cp.roundsSeroState.get(3) == 0)numPigsRound3++;

            if(cp.roundsSeroState.get(4) == 1)numSeroPigsRound4++;
            if(cp.roundsSeroState.get(4) == 1 || cp.roundsSeroState.get(4) == 0)numPigsRound4++;

            if(cp.roundsSeroState.get(5) == 1)numSeroPigsRound5++;
            if(cp.roundsSeroState.get(5) == 1 || cp.roundsSeroState.get(5) == 0)numPigsRound5++;

            //if(cp.roundsSeroState.get(6) == 1)numSeroPigsRound6++;
            //if(cp.roundsSeroState.get(6) == 1 || cp.roundsSeroState.get(6) == 0)numPigsRound6++;
        }


        System.out.println(sim.villageName + " seroPrevalence round 1: " +  (double)numSeroPigsRound1/(double)numPigsRound1);
        System.out.println(sim.villageName + " seroPrevalence round 2: " +  (double)numSeroPigsRound2/(double)numPigsRound2);
        System.out.println(sim.villageName + " seroPrevalence round 3: " +  (double)numSeroPigsRound3/(double)numPigsRound3);
        System.out.println(sim.villageName + " seroPrevalence round 4: " +  (double)numSeroPigsRound4/(double)numPigsRound4);
        System.out.println(sim.villageName + " seroPrevalence round 5: " +  (double)numSeroPigsRound5/(double)numPigsRound5);


        //for(int i = 0; i < sim.householdsBag.size(); i++)
        //{
        //    Household hh = (Household)sim.householdsBag.get(i);
        //    System.out.println(hh.targetNumOfPigs);
        //}

        //System.exit(0);



    }

    //============================================================   
    public void readViajeGATES2(String viaje)
    {
        if(sim.extendedOutput)System.out.println(sim.villageName  + " --------------------------------------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ---- Reading " + viaje  + " GATES2 --------");
        if(sim.extendedOutput)System.out.println(" ");

        getHousesLinesFromPigsFileGATES2Viajes(viaje);

        List<String> line = houseLines.get(0);


        int stats = 0;
        int statsPigs = 0;
        int statsPigsHh = 0;

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


                int numBands = -100;
                if(line.size() > 10)
                {
                    numBands = 0;
                    numBands = numBands + Integer.parseInt(line.get(12));
                    numBands = numBands + Integer.parseInt(line.get(13));
                    numBands = numBands + Integer.parseInt(line.get(14));
                    numBands = numBands + Integer.parseInt(line.get(15));
                    numBands = numBands + Integer.parseInt(line.get(16));
                    numBands = numBands + Integer.parseInt(line.get(17));
                    numBands = numBands + Integer.parseInt(line.get(18));
                    if(line.size() > 19)numBands = numBands + Integer.parseInt(line.get(19));
                }

                if(GATES2Round == 1)
                {
                    Pig pig = new Pig(state, hh, false, false);

                    double rand = state.random.nextDouble();
                    if(rand < sim.importPrev)
                    {
                        sim.pigsGen.assignPigCysts(pig);
                    }

                    pig.GATESID = Integer.parseInt(line.get(1));

                    pig.age = (int)Math.round(sim.weeksInAYear * ( Integer.parseInt(line.get(5)))  );
                    if(printOut)if(sim.extendedOutput)System.out.println ("New Pig age: " + (pig.age));

                    //gender
                    if(line.get(4).equals("0"))pig.gender = "female";
                    else pig.gender = "male";
                    if(printOut)if(sim.extendedOutput)System.out.println ("New Pig gender: " + pig.gender);

                    //if(!line.get(11).equals(sim.villageNameNumber))pig.imported = true;

                    if(line.size() <= 10)continue;

                    if(numBands > 0)pig.seropositive = true;
                    else if(numBands == 0)pig.seropositive = false;
                    else pig.seropositive = null;

                    statsPigs++;
                    statsPigsHh++;

                    if(hh.corralUse.equals("never"))pig.corraled = "never";
                    else if(hh.corralUse.equals("sometimes"))pig.corraled = "sometimes";
                    else if(hh.corralUse.equals("always"))pig.corraled = "always";
                }


                //System.out.println (numBands + " " + GATES2Round);

                Boolean ex = false;
                for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
                {
                    CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);
                    if(cp.ID == Integer.parseInt(line.get(1)))
                    {
                        if(numBands > 0)cp.roundsSeroState.set(GATES2Round, 1);
                        else if(numBands == 0)cp.roundsSeroState.set(GATES2Round, 0);

                        ex = true;
                        break;
                    }
                }

                if(!ex)
                {
                    CensedPig cp = new CensedPig(state, Integer.parseInt(line.get(1)));
                    if(sim.simW.studyVillages)sim.simW.censedPigs.add(cp);
                    if(numBands > 0)cp.roundsSeroState.set(GATES2Round, 1);
                    else if(numBands == 0)cp.roundsSeroState.set(GATES2Round, 0);
                }

                //System.out.println("pig: " + pig.GATESID + " num bands: " + numBands); 

            }

            if(GATES2Round == 1)hh.targetNumOfPigs = statsPigsHh;
            //System.out.println(hh.targetNumOfPigs);

        }


        if(GATES2Round == 1)System.out.println(sim.villageName + ": " + statsPigs + " from round 1 Pigs generated");

        //System.exit(0);


    }


    //============================================================   
    public CensedPig getCensedPigStringID(String cId)
    {
        for(int i = 0; i < sim.censedPigsBag.size(); i++)
        {
            CensedPig p = (CensedPig)sim.censedPigsBag.get(i);
            if(p.IDString.equals(cId))return p;
        }
        return null;
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

    //Read the Pigs file ---------------------------
    public void getHousesLinesFromPigsFileGATES2Viajes(String viaje)
    {
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/populationsData/GATES2/WB_GATES02_Porcino.xlsx";
        sheetName = viaje;
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
                if(line.get(2).equals(sim.villageNameNumber))
                {
                    //if(sim.extendedOutput)System.out.println (line);
                    //System.out.println (line);
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

        //System.exit(0);

    }

    //============================================================   
    public void readPorcinoGATES2Rounds()
    {
        Boolean printOut = false;

        String inputFile = "./inputData/populationsData/GATES2/Gates 2_pigs_AllRounds 04102020.xls";
        String sheetName = "Gates 2 Pigs";

        if(sim.extendedOutput)System.out.println ("");
        if(sim.extendedOutput)System.out.println ("-------------------------------------");
        if(sim.extendedOutput)System.out.println (" ---- Reading pigs serology data ----");
        if(sim.extendedOutput)System.out.println ("---- Data input file: " + inputFile);

        //can be currentVillage or all to analyze the serology data of current 
        //village or all the villages together
        String whatAna = "currentVillage";

        //Initialize the households serology data
        for(int i = 0; i < sim.householdsBag.size(); i ++)
        {
            Household hh = (Household)sim.householdsBag.get(i);

            for(int j = 0; j < 7; j++)
            {
                hh.seropositivePigs.add(0);
                hh.seroSamples.add(0);
                hh.numPigsRound.add(0);
            }
        }

        int statsPigs = 0;

        List<Integer> pigsIds = new ArrayList<Integer>();

        try{
            Workbook workbookFile = WorkbookFactory.create(new FileInputStream(inputFile));

            Sheet sheet = workbookFile.getSheet(sheetName);
            //if(sim.extendedOutput)System.out.println (sheet);
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
                //if(sim.extendedOutput)System.out.println ("nrow: " + statsRows);

                int stats = -1;
                Boolean read = false;

                line = new ArrayList<String>();

                for(Cell cell : row)
                {  
                    statsCells++;

                    String stri = "";

                    switch (cell.getCellType()) {
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

                int age = 0;
                int ageInMonths = 0;
                int roundLenght = 13;
                int location = 0;
                int numBands = 0;
                String wbValue = "";


                if(statsRows > 0)
                {
                    String IDString = line.get(2);

                    CensedPig cp = getCensedPigStringID(IDString);

                    //if(localPrintOut)System.out.println ("pig ID: " + ID);

                    //create the censedPig 
                    if(cp == null)cp = new CensedPig(state, -100);
                    if(sim.simW.studyVillages)sim.simW.censedPigs.add(cp);
                    cp.IDString = IDString;

                    Boolean ana = false;

                    int round = Integer.parseInt(line.get(1));

                    cp.setRoundsCaptured(round, 1);

                    String vill = line.get(3);
                    cp.roundsVillages.set(round, vill);

                    String house = line.get(4);
                    cp.roundsHousehold.set(round, house);

                    cp.gender = line.get(6);

                    //cp age --------------
                    age = (int)Math.round(sim.weeksInAMonth * ( Integer.parseInt(line.get(7))));
                    ageInMonths = Integer.parseInt(line.get(7));

                    cp.addAges(round, age);
                    cp.roundsAgesInMonths.set(round, ageInMonths);

                    if(line.size() < 10)continue;

                    //if(sim.extendedOutput)System.out.println (line);
                    if(line.size() < 10)continue;
                    cp.IDMotherString = line.get(9);

                    wbValue = line.get(location);
                    if(!wbValue.equals(""))cp.roundsNotMissing.set(round, 1);

                    //read the num of positive bands for this round
                    //-100 no data found
                    numBands =-100;
                    if(!wbValue.equals("") && line.size() > 10)
                    {
                        //if(round == 1)System.out.println (wbValue + " " + ID);

                        numBands = 0;

                        //GP50 band excluded
                        //if(line.get(11).equals("1"))numBands++;


                        if(line.get(12).equals("1"))numBands++;

                        if(line.get(13).equals("1"))numBands++;

                        if(line.get(14).equals("1"))numBands++;

                        if(line.get(15).equals("1"))numBands++;

                        if(line.get(16).equals("1"))numBands++;

                        if(line.get(17).equals("1"))numBands++;
                    }

                    if(numBands > 0)cp.roundsSeroState.set(round, 1);
                    else if(numBands == 0)cp.roundsSeroState.set(round, 0);

                    cp.setBands(round, numBands);
                    cp.setWbValue(round, wbValue);

                    //create the sim pigs populations
                    if(round == 1)
                    {
                        if(!sim.villageNameNumber.equals(vill))continue;
                        int houseShpId = Integer.parseInt(house);
                        Household hh = sim.householdsGen.getHouseholdByshpId(houseShpId);
                        if(hh == null)
                        {
                            if(sim.extendedOutput)System.out.println ("---- The household of this pigs was not found");
                            if(sim.extendedOutput)System.out.println ("---- in the households census file");
                            continue;
                        }

                        Pig pig = new Pig(state, hh, false, false);

                        hh.targetNumOfPigs++;
                        hh.pigOwner = true;

                        double rand = state.random.nextDouble();
                        if(rand < sim.importPrev)
                        {
                            sim.pigsGen.assignPigCysts(pig);
                        }

                        pig.GATESID = Integer.parseInt(line.get(1));

                        pig.age = (int)Math.round(sim.weeksInAMonth * ( Integer.parseInt(line.get(7))));
                        if(printOut)if(sim.extendedOutput)System.out.println ("New Pig age: " + (pig.age));

                        //gender
                        pig.gender = line.get(6);
                        if(printOut)if(sim.extendedOutput)System.out.println ("New Pig gender: " + pig.gender);

                        //if(!line.get(11).equals(sim.villageNameNumber))pig.imported = true;

                        if(numBands > 0)pig.seropositive = true;
                        else if(numBands == 0)pig.seropositive = false;

                        statsPigs++;

                        if(hh.corralUse.equals("never"))pig.corraled = "never";
                        else if(hh.corralUse.equals("sometimes"))pig.corraled = "sometimes";
                        else if(hh.corralUse.equals("always"))pig.corraled = "always";


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

        //System.exit(0);

        if(sim.extendedOutput)System.out.println (sim.villageName + " ---- Total number of pigs created: " + statsPigs);

        correctCensedPigsAges();
    }

    //============================================================   
    public void correctCensedPigsAges()
    {
        //-------------------------------------------------------------
        //Creating a correcte age time serie for each pig
        if(sim.extendedOutput)System.out.println("-----------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Creating a corrected time series for pigs ages");

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            //if(cp.excluded)continue;

            int startI = -100;
            int startAge = -10;
            int startAgeInMonths = 0;

            Boolean start = false;

            for(int i = 1; i < 7; i++)
            {
                if(cp.roundsAges.get(i) != -100 && !start)
                {
                    startI = 0;
                    startAge = cp.roundsAges.get(i);
                    startAgeInMonths = cp.roundsAgesInMonths.get(i);
                    start = true;
                }

                if(cp.roundsAges.get(i) != -100 && start)
                {
                    cp.roundsAgesCorrected.set(i, (startAge + (int)Math.round(startI * 2 * sim.weeksInAMonth)));
                    cp.roundsAgesCorrectedInMonths.set(i, (startAgeInMonths + (startI * 2)));
                    startI++;
                }
                if(cp.roundsAges.get(i) == -100 && start)
                {
                    cp.roundsAgesCorrected.set(i, -100);
                    cp.roundsAgesCorrectedInMonths.set(i, -100);
                    startI++;
                }
            }
        }

        //-----------------------------
        /*
        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);
            if(cp.excluded)continue;

            int coho = 0;

            if(sim.extendedOutput)System.out.println("-----------------");
            if(sim.extendedOutput)System.out.println(cp.IDString);
            for(int i = 1; i < 7; i++)
            {
                //if(sim.extendedOutput)System.out.println("round: " + i + " " + cp.roundsAges.get(i) + " " + cp.roundsAgesCorrected.get(i));
                if(sim.extendedOutput)System.out.println("round: " + i + " " + cp.roundsAgesInMonths.get(i) + " " + cp.roundsAgesCorrectedInMonths.get(i));
            }
            if(cp.IDString.equals("31544"))break;
        }
        System.exit(0);
        */



    }

    //============================================================   
    public void writePigsSerologyStats(String what)
    {

        String villageString = "";


        if(sim.extendedOutput)System.out.println("============================================");
        if(sim.extendedOutput)System.out.println("============================================");
        if(what.equals("village"))
        {
            if(sim.extendedOutput)System.out.println("Pigs serology stats for village: " + sim.villageName);
            villageString = "Village " + sim.villageName + " ";
        }
        if(what.equals("all"))
        {
            if(sim.extendedOutput)System.out.println("Pigs serology stats for all GATES2 villages");
            villageString = "All GATES2 villages ";
        }

        double perseroPrevalencePiglets = 0.0;
        double perseroPrevalencePigletsNorm = 0.0;
        double perseroPrevalenceTransition = 0.0;
        double perseroPrevalenceTransitionNorm = 0.0;
        double perseroPrevalenceYoungPigs = 0.0;
        double perseroPrevalenceYoungPigsNorm = 0.0;
        double perseroPrevalenceAdultPigs = 0.0;
        double perseroPrevalenceAdultPigsNorm = 0.0;

        double seroprevalence = 0.0;
        double seroprevalenceNorm = 0.0;

        for(int i = 0; i < 1000; i++)
        {
            prevalenceByAge.add(0.0);
            prevalenceByAgeNorm.add(0.0);
        }

        int stats = 0;

        for(int round = 1; round < 7; round++)
        {
            perseroPrevalencePiglets = 0.0;
            perseroPrevalencePigletsNorm = 0.0;
            perseroPrevalenceTransition = 0.0;
            perseroPrevalenceTransitionNorm = 0.0;
            perseroPrevalenceYoungPigs = 0.0;
            perseroPrevalenceYoungPigsNorm = 0.0;
            perseroPrevalenceAdultPigs = 0.0;
            perseroPrevalenceAdultPigsNorm = 0.0;

            seroprevalence = 0.0;
            seroprevalenceNorm = 0.0;

            for(int i = 0; i < 1000; i++)
            {
                prevalenceByAge.set(i, 0.0);
                prevalenceByAgeNorm.set(i, 0.0);
            }



            //if(i == 1 || i == 2 || i == 3)continue;


            for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
            {
                CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

                //cp.printResume();

                if(what.equals("village") && !cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;

                int ageM = cp.roundsAgesCorrectedInMonths.get(round);
                //int ageM = cp.roundsAgesInMonths.get(i);

                if(ageM == -100)continue;

                //if(i == 2 || i == 3)continue;

                //if(sim.extendedOutput)System.out.println(ageM);

                if(cp.roundsBands.get(round) != -100)
                {
                    prevalenceByAgeNorm.set(ageM, (prevalenceByAgeNorm.get(ageM) + 1.0));
                    if(cp.roundsBands.get(round) >  0)prevalenceByAge.set(ageM, (prevalenceByAge.get(ageM) + 1.0));
                    stats++;


                    if(ageM >= 2 && ageM <= 3)
                    {
                        perseroPrevalencePigletsNorm++;
                        if(cp.roundsBands.get(round) > 0)perseroPrevalencePiglets++;
                    }

                    if(ageM == 4)
                    {
                        perseroPrevalenceTransitionNorm++;
                        if(cp.roundsBands.get(round) > 0)perseroPrevalenceTransition++;
                    }

                    if(ageM >= 5 && ageM <= 9)
                    {
                        perseroPrevalenceYoungPigsNorm++;
                        if(cp.roundsBands.get(round) > 0)perseroPrevalenceYoungPigs++;
                    }

                    if(ageM >= 10)
                    {
                        perseroPrevalenceAdultPigsNorm++;
                        if(cp.roundsBands.get(round) > 0)perseroPrevalenceAdultPigs++;
                    }

                    seroprevalenceNorm++;
                    if(cp.roundsBands.get(round) > 0)seroprevalence++;
                }

            }



            //normalize
            List<Double> positives = new ArrayList<Double>(prevalenceByAge);
            double statsTot = 0;
            for(int i = 0; i < 1000; i++)
            {
                prevalenceByAge.set(i, (prevalenceByAge.get(i)/prevalenceByAgeNorm.get(i)));
                statsTot = statsTot + prevalenceByAgeNorm.get(i);
            }

            //if(sim.extendedOutput)System.out.println(sim.villageName + " ");

            if(sim.extendedOutput)System.out.println(villageString + " ---- Round: "+ round + " ----------");

            if(1 != 1)
            {
                if(sim.extendedOutput)System.out.println(villageString + " ---- Prevalence by pig age:");
                if(sim.extendedOutput)System.out.println("age-in-weeks age-in-months num-pigs-per-age-segment num-seropositive-pigs-per-age-segment prop-pigs-per-age-segment seroprev-pigs-per-age-segment" );
                for(int i = 0; i < 1000; i++)
                {
                    if(prevalenceByAgeNorm.get(i) > 0.0)
                    {
                        if(sim.extendedOutput)System.out.println(i * sim.weeksInAMonth
                                + " " + i
                                + " " + prevalenceByAgeNorm.get(i) 
                                + " " + positives.get(i) 
                                + " " + prevalenceByAgeNorm.get(i)/(double)statsTot
                                + " " + prevalenceByAge.get(i));
                    }

                }
            }

            //if(sim.extendedOutput)System.out.println(villageString + " ");
            if(sim.extendedOutput)System.out.println(villageString + " ---- Prevalence by pigs age segments:");
            String tmp = " Avg. overall pigs seroprevalence: ";
            if(sim.extendedOutput)System.out.println(villageString + tmp +  seroprevalence/(double)seroprevalenceNorm);

            tmp = " Avg. observed Piglets Seroprevalence with the seroincidence cohorts periodicity (7 <= age <= 14 weeks): ";
            if(sim.extendedOutput)System.out.println(villageString + tmp +  perseroPrevalencePiglets/(double)perseroPrevalencePigletsNorm);

            tmp = " Avg. observed Pigs Seroprevalence in the transition age segment with the seroincidence cohorts periodicity (15 <= age <= 20): ";
            if(sim.extendedOutput)System.out.println(villageString + tmp +  perseroPrevalenceTransition/(double)perseroPrevalenceTransitionNorm);

            tmp = " Avg. observed Young Pigs Seroprevalence with the seroincidence cohorts periodicity (21 <= age <= 37 weeks): ";
            if(sim.extendedOutput)System.out.println(villageString + tmp +  perseroPrevalenceYoungPigs/(double)perseroPrevalenceYoungPigsNorm);

            tmp = " Avg. observed Adult Pigs Seroprevalence with the seroincidence cohorts periodicity (38 <= age weeks): ";
            if(sim.extendedOutput)System.out.println(villageString + tmp +  perseroPrevalenceAdultPigs/(double)perseroPrevalenceAdultPigsNorm);

            if(sim.extendedOutput)System.out.println(" ");

        }


    }


    //============================================================   
}
