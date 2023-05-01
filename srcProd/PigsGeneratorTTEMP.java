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


public class PigsGeneratorTTEMP implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public boolean printOut = true;

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

    public int statsVillaProcessed = 0;
    public int statsHouse = 0;
    public int statsLineProcessed = 0;

    //sero incidences and prevalences for the entire TTEMP trial
    public List<Double> seroIncidencePigsRoundsAll = new ArrayList<Double>();
    public List<Double> seroPrevalencePigletsRoundsAll = new ArrayList<Double>();
    public List<Double> seroPrevalencePigsRoundsAll = new ArrayList<Double>();
    public List<Double> overallSeroPrevalencePigsRoundsAll = new ArrayList<Double>();

    public List<Double> prevalenceByAge = new ArrayList<Double>();
    public List<Double> prevalenceByAgeNorm = new ArrayList<Double>();

    //====================================================
    public PigsGeneratorTTEMP(final SimState pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;
    }

    //====================================================
    public void step(final SimState state)
    {

    }

    //============================================================   
    public void generatePigsFromDataTTEMP()
    {
        getHousesLinesFromPigsFileTTEMP();

        List<String> line = houseLines.get(0);

        int stats = 0;
        int statsPigs = 0;
        int statsPigsHh = 0;
        int statsNotFoundHh = 0;
        //if(sim.extendedOutput)System.out.println ("Househols size(): " + sim.householdsBag.size());
        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);
            if(printOut)if(sim.extendedOutput)System.out.println ("-------------- House: " + hh.shpId);
            //if(!hh.pigOwner)continue;

            statsPigsHh = 0;

            houseLines = housesLinesMap.get(hh.shpId);

            stats++;
            if(houseLines != null)hh.pigOwner = true;
            else 
            {
                if(printOut)if(sim.extendedOutput)System.out.println ("Househols shpId number: " + hh.shpId + " not found in serology file");
                statsNotFoundHh++;
                continue;
            }

            if(printOut)if(sim.extendedOutput)System.out.println ("Househols shpId number: " + hh.shpId);
            if(printOut)if(sim.extendedOutput)System.out.println ("Number of pigs: " + houseLines.size());

            for(int f = 0; f < houseLines.size(); f++)
            {
                line = (List<String>)houseLines.get(f);
                //if(sim.extendedOutput)System.out.println (line.get(46));
                //if(line.get(7).equals(""))continue;//this is for round 1 pigs
                //if(line.get(20).equals(""))continue;//this is for round 2 pigs
                //if(line.get(33).equals(""))continue;//this is for round 3 pigs
                if(line.get(46).equals(""))continue;//this is for round 4 pigs

                Pig pig = new Pig(state, hh, false, false);
                statsPigsHh++;

                //4.35 is the average number of week per month
                pig.age = (int)Math.round(4.35 * ( Integer.parseInt(line.get(47))) );
                //if(printOut)if(sim.extendedOutput)System.out.println ("New Pig age: " + (pig.age));

                pig.censusIdentity =  Integer.parseInt(line.get(4));
                //if(printOut)if(sim.extendedOutput)System.out.println ("New Pig censusIdentity: " + (pig.censusIdentity));

                if(line.get(53).equals("1") || line.get(54).equals("1")
                        || line.get(55).equals("1") || line.get(56).equals("1")
                        || line.get(57).equals("1") || line.get(58).equals("1")
                  )
                {
                    pig.seropositive = true;
                    numSeropositivePigsVillage++;
                }
                //if(printOut)if(sim.extendedOutput)System.out.println ("New Pig seropositive: " + (pig.seropositive));

                //gender
                if(line.get(5).equals("0"))pig.gender = "female";
                else pig.gender = "male";
                //if(printOut)if(sim.extendedOutput)System.out.println ("New Pig gender: " + pig.gender);

                //if(!line.get(11).equals(sim.villageNameNumber))pig.imported = true;

                statsPigs++;

                if(hh.corralUse.equals("never"))pig.corraled = "never";
                else if(hh.corralUse.equals("sometimes"))
                {
                    pig.corraled = "sometimes";
                    //if(sim.extendedOutput)System.out.println ("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                    //System.exit(0);
                }
                else if(hh.corralUse.equals("always"))pig.corraled = "always";

            }

            if(hh.pigs.size() < hh.targetNumOfPigs)
            {
                int diff = hh.targetNumOfPigs - hh.pigs.size();
                if(printOut)if(sim.extendedOutput)System.out.println ("diff pigs: " + diff);
                for(int p = 0; p < diff; p++)
                {
                    Pig pig = new Pig(state, hh, false, false);
                    statsPigsHh++;

                    pig.age = (int)Math.round(pig.slaughterAge * (1 - state.random.nextDouble()));

                    pig.censusIdentity =  -100;

                    //gender
                    int irand = state.random.nextInt(2);
                    if(line.get(5).equals("0"))pig.gender = "female";
                    else pig.gender = "male";
                    //if(printOut)if(sim.extendedOutput)System.out.println ("New Pig gender: " + pig.gender);

                    //if(!line.get(11).equals(sim.villageNameNumber))pig.imported = true;

                    statsPigs++;

                    if(hh.corralUse.equals("never"))pig.corraled = "never";
                    else if(hh.corralUse.equals("sometimes"))pig.corraled = "sometimes";
                    else if(hh.corralUse.equals("always"))pig.corraled = "always";

                }
            }

            if(printOut)if(sim.extendedOutput)System.out.println(sim.villageName + " House num: " + hh.shpId + " target num of pigs: " + hh.targetNumOfPigs); 
            if(printOut)if(sim.extendedOutput)System.out.println(sim.villageName + " House num: " + hh.shpId + " num of pigs generated: " + statsPigsHh); 


        }
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + " households in serosurvey not found in shp file: " + statsNotFoundHh + " over " + sim.householdsBag.size()); 
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + statsPigs + " Pigs generated");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + numSeropositivePigsVillage + " seropositive Pigs generated");
        //if(sim.extendedOutput)System.out.println(sim.villaeeName + ": num householdslines: " + stats); 
        //System.exit(0);
    }

    //Read the Pigs file ---------------------------
    public void getHousesLinesFromPigsFileTTEMP()
    {
        if(sim.extendedOutput)System.out.println ("---------------------------------");
        if(sim.extendedOutput)System.out.println ("---- Reading the pigs serology data  ----");
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/populationsData/TTEMP/TTEMP_PigSerologyandTreatment_2015.xls";
        sheetName = "TTEMP Pig serology 2015";
        if(sim.extendedOutput)System.out.println ("Survey input file: " + inputFile);

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

                if(statsRows > 0)
                {
                    //only round 4 data
                    if(!line.get(46).equals("1"))continue;

                    //get the pigs that were trated during ring interventions
                    if(!line.get(59).equals("") || !line.get(60).equals("")
                            || !line.get(65).equals("") || !line.get(66).equals("")
                            || !line.get(71).equals("") || !line.get(72).equals(""))
                    {
                        treatedPigsAllVillages.put(Integer.parseInt(line.get(4)), true);
                        totNumTreatedPigsAllVillages++;
                    }
                    else treatedPigsAllVillages.put(Integer.parseInt(line.get(4)), false);

                    if(line.get(53).equals("1") || line.get(54).equals("1")
                            || line.get(55).equals("1") || line.get(56).equals("1")
                            || line.get(57).equals("1") || line.get(58).equals("1")
                      )
                    {
                        seropositivePigsAllVillages.put(Integer.parseInt(line.get(4)), true);
                        numSeropositivePigsAllVillages++;
                    }
                    else seropositivePigsAllVillages.put(Integer.parseInt(line.get(4)), false);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                if(line.get(2).equals(sim.villageNameNumber))
                {
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
    public void readNecroscopyDataTTEMP()
    {
        getPigsLinesFromPigsFileTTEMP();
        //System.exit(0);

        List<String> line = pigsLines.get(0);

        int stats = 0;
        numNecroVilla = 0;
        numNecroAll = 0;
        int notFoundPigs = 0;
        int notFoundPigsAll = 0;
        int diffAge = 0;
        int diffHouse = 0;
        int diffGender = 0;

        for(int i = 0; i < pigsLines.size(); i++)
        {
            if(i == 0)continue;//titles

            line = pigsLines.get(i);
            //if(!hh.pigOwner)continue;

            //if(line.get(2).equals("1"))continue;//intervention group pigs
            //if(sim.extendedOutput)System.out.println(line);

            //Boolean sameVilla = true;
            Boolean sameVilla = false;
            if(sim.villageNameNumber.equals(line.get(3)))sameVilla = true;

            int cId = Integer.parseInt(line.get(5));

            SeroPig sPig = new SeroPig(sim); 
            sPig.pigId = cId;

            sPig.numViableCysts = Integer.parseInt(line.get(12));
            sPig.numDegCysts = Integer.parseInt(line.get(13)) + Integer.parseInt(line.get(14));

            sPig.necroscopy = true;

            sim.seroPigList.add(sPig);

            //if(sim.extendedOutput)System.out.println(cId);
            //if(sim.extendedOutput)System.out.println(treatedPigsAllVillages.size());

            if(treatedPigsAllVillages.get(cId) != null)
            {
                if(treatedPigsAllVillages.get(cId) == true)continue;//control arm intervenined pigs
            }
            else
            {
                notFoundPigsAll++;
            }
            numNecroAll++;

            if(sameVilla)
            {
                Pig pig = getPig(cId);
                if(pig == null)
                {
                    notFoundPigs++;
                    continue;
                }

                numNecroVilla++;

                int age = (int)Math.round(4.35 * ( Integer.parseInt(line.get(7)))  );
                if(age != pig.age)
                {
                    if(sim.extendedOutput)System.out.println("Pig and pig necroscopy ages differ");
                    if(sim.extendedOutput)System.out.println("Necro age: " + age + " serology age: " + pig.age);
                    diffAge++;
                }


                int house = Integer.parseInt(line.get(4));
                if(house != pig.household.shpId)
                {
                    if(sim.extendedOutput)System.out.println("Pig and pig necroscopy households differ");
                    if(sim.extendedOutput)System.out.println("Necro households: " + house + " serology households: " + pig.household.shpId);
                    diffHouse++;
                }
                else
                {
                    if(Integer.parseInt(line.get(15)) > 0)
                    {
                        pig.household.numInfPigs++;
                    }

                }

                String gender = "";
                if(line.get(7).equals("0"))gender = "female";
                else gender = "male";
                if(!gender.equals(pig.gender))
                {
                    if(sim.extendedOutput)System.out.println("Pig and pig necroscopy gender differ");
                    if(sim.extendedOutput)System.out.println("Necro gender: " + gender + " serology gender: " + pig.gender);
                    diffGender++;
                }

                if(Integer.parseInt(line.get(15)) > 0)sim.baselineCystiInfPigsVillage++;


                pigsNViableCystsVillage.add(Integer.parseInt(line.get(12)));
                pigsNDegeneratedCystsVillage.add(Integer.parseInt(line.get(14))
                        + Integer.parseInt(line.get(13))
                        );
            }

            pigsWeights.add(Integer.parseInt(line.get(8)));
            pigsTongues.add(Integer.parseInt(line.get(9)));
            pigsNBands.add(Integer.parseInt(line.get(10)));
            pigsGradeOfInfection.add(Integer.parseInt(line.get(11)));
            pigsNViableCysts.add(Integer.parseInt(line.get(12)));
            pigsNDegeneratedCysts.add(Integer.parseInt(line.get(14))
                    + Integer.parseInt(line.get(13))
                    );

            //if(sim.extendedOutput)System.out.println ("Househols shpId number: " + hh.shpId);
            //if(sim.extendedOutput)System.out.println (houseLines.size());
        }

        //to get the preve of cysticercosis in the village we must divede by 
        //the number of seropositive pigs in the village because pigs for
        //necroscopy were selected only among seropositive pigs
        //if(sim.extendedOutput)System.out.println(sim.baselineCystiInfPigsVillage);
        //if(sim.extendedOutput)System.out.println(numNecroVilla);
        //if(sim.extendedOutput)System.out.println(numSeropositivePigsVillage);
        //if(sim.extendedOutput)System.out.println(sim.pigsBag.size());
        //Explained better: I evaluated the prevalence in the seropositive 
        //group as:
        //sim.baselineCystiInfPigsVillage / numNecroVilla
        //then I know that in the remaining part of the not seropositive pigs the 
        //prevalence is zero. The prevalence over the entire population
        //of pigs in the village is the weighted average of the prev in the
        //seropositive group and the not seropositive group being the weight for
        //the seropositive group: numSeropositive/ nun pigs village
        //thne the formula here below.
        if(sim.extendedOutput)System.out.println(sim.villageName + ": village num infected pigs from necroscopy: " + sim.baselineCystiInfPigsVillage);
        sim.baselineCystiInfPigsVillage = sim.baselineCystiInfPigsVillage/(double)numNecroVilla;
        sim.baselineCystiInfPigsVillage = sim.baselineCystiInfPigsVillage * (double)numSeropositivePigsVillage;
        sim.baselineCystiInfPigsVillage = sim.baselineCystiInfPigsVillage / (double)sim.pigsBag.size();

        if(sim.extendedOutput)System.out.println(sim.villageName + ": village baseline cysticercosis infections prevalence: " + sim.baselineCystiInfPigsVillage);
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + numNecroAll + " pigs necroscopies found");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + numNecroVilla + " pigs necroscopies found for current village");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + numSeropositivePigsVillage + " num seropositive pigs in the village");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + sim.pigsBag.size() + " num pigs in the village");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + notFoundPigsAll + " pigs necroscopy ids not found in serology dataset (control + intervention)");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + notFoundPigs + " pigs necroscopy ids not found in serology dataset (only willvage " + sim.villageName  + ")");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + diffAge + " pigs age of necroscopy and serology datasets differ");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + diffHouse + " pigs House of necroscopy and serology datasets differ");
        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + diffGender + " pigs gender of necroscopy and serology datasets differ");
        //if(sim.extendedOutput)System.out.println(sim.villageName + ": num householdslines: " + stats); 
        //System.exit(0);
    }

    //Read the Pigs file ---------------------------
    public void getPigsLinesFromPigsFileTTEMP()
    {
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/populationsData/TTEMP/TTEMP_Necropsy_data_raw_05282020.xls";
        sheetName = "data";
        if(sim.extendedOutput)System.out.println ("Survey input file: " + inputFile);

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

                //if(sim.extendedOutput)System.out.println (line);
                pigsLines.add(line);
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

    //============================================================   
    public void generateHistoCystsFromData(String what)
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " Generating histo from data " + what);
        HashMap<Integer, Double> histo = new HashMap <Integer, Double>();
        //init histo
        histo.put(0, 0.0);
        for(int i = 0; i < sim.numBinsPigCystsHisto; i++)
        {
            int nB = (int)Math.round((double)(i + 1) * (double)sim.extensionBinsPigCystsHisto);
            histo.put(nB, 0.0);
        }

        //fill histo
        int stats = 0;
        int size = 0;
        if(what.equals("all"))size = pigsNViableCysts.size();
        else if(what.equals("village"))size = pigsNViableCystsVillage.size();
        for(int i = 0; i < size; i++)
        {
            int numCysts = 0;
            if(what.equals("all"))numCysts = pigsNViableCysts.get(i) + pigsNDegeneratedCysts.get(i);
            else if(what.equals("village"))numCysts = pigsNViableCystsVillage.get(i) + pigsNDegeneratedCystsVillage.get(i);
            //if(numCysts == 0)stats++;
            //if(numCysts == 0)continue;

            int nB = (int)Math.ceil((double)numCysts/(double)sim.extensionBinsPigCystsHisto);
            nB = (int)((double)nB * (double)sim.extensionBinsPigCystsHisto);

            if(numCysts == 0 || nB == 0)
            {
                double tmp = histo.get(0);
                tmp++;    
                histo.put(0, tmp);
                stats++;
            }
            else if(nB >= sim.numBinsPigCystsHisto * sim.extensionBinsPigCystsHisto)
            {
                //if(sim.extendedOutput)System.out.println(nB);
                //if(sim.extendedOutput)System.out.println("---- Pig number of cyst at slaughter: " + numCysts);
                //if(sim.extendedOutput)System.out.println("---- this number exceeds the max number of cysts in pig cysts histogram");
                double tmp = histo.get(sim.numBinsPigCystsHisto * sim.extensionBinsPigCystsHisto);
                tmp++;    
                histo.put((sim.numBinsPigCystsHisto * sim.extensionBinsPigCystsHisto), tmp);
            }
            else
            {
                double tmp = histo.get(nB);
                tmp++;    
                histo.put(nB, tmp);
            }


        }

        //add the pigs withoutnecroscpy all those pigs are assumed cysts free
        //int remainingPigs = seropositivePigsAllVillages.size() - totNumTreatedPigsAllVillages -  pigsNViableCysts.size();
        //if(sim.extendedOutput)System.out.println(seropositivePigsAllVillages.size());
        //if(sim.extendedOutput)System.out.println(totNumTreatedPigsAllVillages);
        //if(sim.extendedOutput)System.out.println(pigsNViableCysts.size());
        //if(sim.extendedOutput)System.out.println("remainingPigs: " + remainingPigs);
        //for(int i = 0; i < remainingPigs; i++)
        //{
        //    double tmp = histo.get(0);
        //    tmp++;    
        //    histo.put(0, tmp);
        //}


        //if(sim.extendedOutput)System.out.println((double)seropositivePigsAllVillages.size());
        //if(sim.extendedOutput)System.out.println(numSeropositivePigsAllVillages);
        //if(sim.extendedOutput)System.out.println("ratio " + (double)1/(double)seropositivePigsAllVillages.size()/(double)numSeropositivePigsAllVillages);
        for(Integer nC : histo.keySet())
        {
            if(nC == 0)continue;

            double numOcc = histo.get(nC);

            if(what.equals("village"))
            {
                numOcc =  numOcc / (double)numNecroVilla;
                numOcc =  (double)numSeropositivePigsVillage * numOcc;
                numOcc =  numOcc/(double)sim.pigsBag.size();
            }
            else if(what.equals("all"))
            {
                numOcc =  numOcc / (double)numNecroAll;
                numOcc =  (double)numSeropositivePigsAllVillages * numOcc;
                numOcc =  numOcc/1237.0;//1237.0 is the tot number from
                //household census of TTEMP trial

            }



            //numOcc = numOcc * (double)numSeropositivePigsAllVillages;
            //numOcc = numOcc / (double)seropositivePigsAllVillages.size();

            histo.put(nC, numOcc);
        }
        //System.exit(0);


        //if(sim.extendedOutput)System.out.println(histo.get(0));

        //Norm histo
        //SortedSet<Integer> keySet = null;
        //if(sim.extendedOutput)System.out.println("stats " + stats);
        //if(sim.extendedOutput)System.out.println(histo.get(0));
        double norm = 0.0;
        for(Integer nC : histo.keySet())
        {
            if(nC == 0)continue;
            double numOcc = histo.get(nC);
            //if(sim.extendedOutput)System.out.println(nC + " " + numOcc);
            norm = norm + numOcc;
        }

        /*
           for(Integer nC : histo.keySet())
           {
           double numOcc = histo.get(nC);
           if(numOcc > 0.0)
           {
           numOcc = numOcc/norm;
           }
           else numOcc = 0.0;

           histo.put(nC, numOcc);
        //if(sim.extendedOutput)System.out.println("pippo");
           }
           */


        histo.put(0, (1 - norm));

        if(what.equals("all"))sim.pigCystsHisto = histo;
        else if(what.equals("village"))sim.pigCystsHistoVillage = histo;

        keySet = new TreeSet<>(histo.keySet());

        writePigCystsHisto(what);
    }

    //====================================================
    public void writePigCystsHisto(String what)
    {
        //erase_file();

        //if(sim.extendedOutput)System.out.println("================================================");
        //if(sim.extendedOutput)System.out.println ("Writing  outputs ..............................");
        //if(sim.extendedOutput)System.out.println(" ");

        HSSFSheet sheet = null;

        String sheetName = "TTEMP Necroscopy Histo";


        if(what.equals("all"))
        {
            sheet = workbookHistoAll.getSheet(sheetName);
            //If the sheet !exists a new sheet is created --------- 
            if(sheet == null)sheet = workbookHistoAll.createSheet(sheetName);

            workbookHistoAll.setSheetOrder(sheetName, 0);
        }
        else if(what.equals("village"))
        {
            sheet = workbookHistoVillage.getSheet(sheetName);
            //If the sheet !exists a new sheet is created --------- 
            if(sheet == null)sheet = workbookHistoVillage.createSheet(sheetName);

            workbookHistoVillage.setSheetOrder(sheetName, 0);

        }


        Cell cell = null;

        int lastRow = sheet.getLastRowNum();

        //if(sim.extendedOutput)System.out.println("Last row:" + lastRow);
        //lastRow++;

        Row row = sheet.createRow(lastRow++);

        int cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Num Cysts");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Frequency");


        int stats = 0;
        for(Integer nC : keySet)
        {
            double freq = 0.0;
            if(what.equals("all"))freq = sim.pigCystsHisto.get(nC);
            else if(what.equals("village"))freq = sim.pigCystsHistoVillage.get(nC);

            row = sheet.createRow(lastRow);
            lastRow++;
            cellnum = 0;

            cell = row.createCell(cellnum);
            cell.setCellValue((Integer)(nC));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((Double)(freq));
            cellnum++;
        }
        //System.exit(0);


        //copy 
        sim.pigCystsHistoObs = new HashMap<Integer, Double>(sim.pigCystsHistoVillage);


    }


    //============================================================   
    public void generateHistoCystsFromDataProg(String what)
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " Generating histo from data prog " + what);
        HashMap<Integer, Double> histo = new HashMap <Integer, Double>();
        //init histo
        histo.put(0, 0.0);
        histo.put(10, 0.0);
        histo.put(100, 0.0);
        histo.put(1000, 0.0);
        histo.put(10000, 0.0);
        histo.put(100000, 0.0);
        histo.put(1000000, 0.0);

        //fill histo
        int stats = 0;
        int size = 0;

        if(what.equals("all"))size = pigsNViableCysts.size();
        else if(what.equals("village"))size = pigsNViableCystsVillage.size();

        //if(sim.extendedOutput)System.out.println("Size: " + size);

        for(int i = 0; i < size; i++)
        {
            int numCysts = 0;
            if(what.equals("all"))numCysts = pigsNViableCysts.get(i) + pigsNDegeneratedCysts.get(i);
            else if(what.equals("village"))numCysts = pigsNViableCystsVillage.get(i) + pigsNDegeneratedCystsVillage.get(i);
            //if(numCysts == 0)stats++;
            //if(numCysts == 0)continue;

            if(numCysts == 0)
            {
                double tmp = histo.get(0);
                tmp++;    
                histo.put(0, tmp);
                stats++;
            }
            else if(numCysts > 0 && numCysts <= 10)
            {
                double tmp = histo.get(10);
                tmp++;    
                histo.put(10, tmp);
            }
            else if(numCysts > 10 && numCysts <= 100)
            {
                double tmp = histo.get(100);
                tmp++;    
                histo.put(100, tmp);
            }
            else if(numCysts > 100 && numCysts <= 1000)
            {
                double tmp = histo.get(1000);
                tmp++;    
                histo.put(1000, tmp);
            }
            else if(numCysts > 1000 && numCysts <= 10000)
            {
                double tmp = histo.get(10000);
                tmp++;    
                histo.put(10000, tmp);
            }
            else if(numCysts > 10000 && numCysts <= 100000)
            {
                double tmp = histo.get(100000);
                tmp++;    
                histo.put(100000, tmp);
            }
            else if(numCysts > 100000)
            {
                double tmp = histo.get(1000000);
                tmp++;    
                histo.put(1000000, tmp);
            }


        }

        //if(sim.extendedOutput)System.out.println("stats: " + stats);

        //add the pigs withoutnecroscpy all those pigs are assumed cysts free
        //if(sim.extendedOutput)System.out.println(histo.get(0));

        //int remainingPigs = seropositivePigsAllVillages.size() - totNumTreatedPigsAllVillages -  pigsNViableCysts.size();
        //if(sim.extendedOutput)System.out.println(remainingPigs);
        //if(sim.extendedOutput)System.out.println(seropositivePigsAllVillages.size());
        //if(sim.extendedOutput)System.out.println(totNumTreatedPigsAllVillages);
        //if(sim.extendedOutput)System.out.println(pigsNViableCysts.size());
        //System.exit(0);
        //for(int i = 0; i < remainingPigs; i++)
        //{
        //    double tmp = histo.get(0);
        //    tmp++;    
        //    histo.put(0, tmp);
        //}

        //if(sim.extendedOutput)System.out.println((double)seropositivePigsAllVillages.size());
        //if(sim.extendedOutput)System.out.println(numSeropositivePigsAllVillages);
        //if(sim.extendedOutput)System.out.println("ratio " + (double)1/(double)seropositivePigsAllVillages.size()/(double)numSeropositivePigsAllVillages);
        //if(sim.extendedOutput)System.out.println("---------------------------");
        for(Integer nC : histo.keySet())
        {
            double numOcc = histo.get(nC);
            //if(sim.extendedOutput)System.out.println("nC: " + nC + " numOcc: " + numOcc);

            if(nC == 0)continue;

            if(what.equals("village"))
            {
                numOcc =  numOcc / (double)numNecroVilla;
                numOcc =  (double)numSeropositivePigsVillage * numOcc;
                numOcc =  numOcc/(double)sim.pigsBag.size();
            }
            else if(what.equals("all"))
            {
                numOcc =  numOcc / (double)numNecroAll;
                numOcc =  (double)numSeropositivePigsAllVillages * numOcc;
                numOcc =  numOcc/1237.0;//1237.0 is the tot number from
                //household census of TTEMP trial

            }

            //numOcc = numOcc * (double)numSeropositivePigsAllVillages;
            //numOcc = numOcc / (double)seropositivePigsAllVillages.size();

            //if(sim.extendedOutput)System.out.println("nC: " + nC + " numOcc: " + numOcc);
            histo.put(nC, numOcc);
        }
        //System.exit(0);



        //Norm histo
        //SortedSet<Integer> keySet = null;
        //if(sim.extendedOutput)System.out.println("stats " + stats);
        //if(sim.extendedOutput)System.out.println(histo.get(0));
        double norm = 0.0;
        for(Integer nC : histo.keySet())
        {
            if(nC == 0)continue;
            double numOcc = histo.get(nC);
            //if(sim.extendedOutput)System.out.println(nC + " " + numOcc);
            norm = norm + numOcc;
        }

        //for(Integer nC : histo.keySet())
        //{
        //    double numOcc = histo.get(nC);
        //    if(numOcc > 0.0)numOcc = numOcc/norm;
        //    else numOcc = 0.0;

        //    histo.put(nC, numOcc);
        //    //if(sim.extendedOutput)System.out.println("pippo");

        //}

        histo.put(0, (1 - norm));

        if(what.equals("all"))sim.pigCystsHistoProg = histo;
        else if(what.equals("village"))sim.pigCystsHistoProgVillage = histo;

        keySet = new TreeSet<>(histo.keySet());

        writePigCystsHistoProg(what);
        //System.exit(0);

    }

    //====================================================
    public void writePigCystsHistoProg(String what)
    {
        //erase_file();

        //if(sim.extendedOutput)System.out.println("================================================");
        //if(sim.extendedOutput)System.out.println ("Writing  outputs ..............................");
        //if(sim.extendedOutput)System.out.println(" ");

        HSSFSheet sheet = null;

        String sheetName = "TTEMP Necr. Progr. Histo";

        if(what.equals("all"))
        {
            sheet = workbookHistoAll.getSheet(sheetName);
            //If the sheet !exists a new sheet is created --------- 
            if(sheet == null)sheet = workbookHistoAll.createSheet(sheetName);

            workbookHistoAll.setSheetOrder(sheetName, 0);
        }
        else if(what.equals("village"))
        {
            sheet = workbookHistoVillage.getSheet(sheetName);
            //If the sheet !exists a new sheet is created --------- 
            if(sheet == null)sheet = workbookHistoVillage.createSheet(sheetName);

            workbookHistoVillage.setSheetOrder(sheetName, 0);

        }

        //workbook.setSheetOrder(sheetName, 0);

        Cell cell = null;

        int lastRow = sheet.getLastRowNum();

        //if(sim.extendedOutput)System.out.println("Last row:" + lastRow);
        //lastRow++;

        Row row = sheet.createRow(lastRow++);

        int cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Num Cysts");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Frequency");


        int stats = 0;
        for(Integer nC : keySet)
        {
            double freq = 0.0;
            if(what.equals("all"))freq = sim.pigCystsHistoProg.get(nC);
            else if(what.equals("village"))freq = sim.pigCystsHistoProgVillage.get(nC);

            row = sheet.createRow(lastRow);
            lastRow++;
            cellnum = 0;

            cell = row.createCell(cellnum);
            cell.setCellValue((Integer)(nC));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((Double)(freq));
            cellnum++;
        }
        //System.exit(0);

        //copy 
        sim.pigCystsHistoProgObs = new HashMap<Integer, Double>(sim.pigCystsHistoProgVillage);

    }



    //====================================================
    public void writePigCystsHistoToFile(String what)
    {
        String file_name = "";
        if(what.equals("all"))file_name = "./outputs/" + sim.simName + "_necroscopyData_all.xls";
        else if(what.equals("village"))file_name = "./outputs/" + sim.simName + "_necroscopyData_" + sim.villageName + ".xls";

        try {

            FileOutputStream out = 
                new FileOutputStream(new File(file_name));
            if(what.equals("all"))workbookHistoAll.write(out);
            else if(what.equals("village"))workbookHistoVillage.write(out);
            out.close();
            if(sim.extendedOutput)System.out.println(sim.villageName  + " output spreadsheet written sucessfully. " + what);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.exit(0);



    }

    //===============================================
    public void infectPigsBaselineTTEMP()
    {
        int stats = 0;
        int statsNo = 0;

        for(int i = 1; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            pig.numCysts = 0;

            double random = state.random.nextDouble();
            if(random > sim.baselineCystiInfPigsEntireDataset) continue;
            stats++;

            sim.pigsGen.assignPigCysts(pig);

            //if(sim.extendedOutput)System.out.println ("num cysts from baseline infect TTEMP pigs: " + pig.numCysts);
        }

        //if(sim.extendedOutput)System.out.println(stats);
        //if(sim.extendedOutput)System.out.println(statsNo);
        stats = 0;
        for(int i = 1; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);
            //if(sim.extendedOutput)System.out.println ("num cysts from baseline infect TTEMP pigs: " + pig.numCysts);
            if(pig.numCysts > 0)stats++;
        }
        //if(sim.extendedOutput)System.out.println(stats);
    }

    //===============================================
    public void readSerologyTTEMP()
    {
        Boolean printOut = true;

        String inputFile = "./inputData/populationsData/TTEMP/TTEMP_PigSerologyandTreatment_2015.xls";
        String sheetName = "TTEMP Pig serology 2015";

        if(sim.extendedOutput)System.out.println ("");
        if(sim.extendedOutput)System.out.println (sim.villageNameNumber + " Reading pigs serology data ----");
        if(sim.extendedOutput)System.out.println ("---- Data input file: " + inputFile);

        //can be currentVillage or all to analyze the serology data of current 
        //village or all the villages together
        String whatAna = "currentVillage";

        List<Integer> pigsIds = new ArrayList<Integer>();

        //int statsCp = 0;

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
                    int ID = Integer.parseInt(line.get(4));
                    //if(localPrintOut)System.out.println ("pig ID: " + ID);

                    //create the censedPig 
                    CensedPig cp = new CensedPig(state, ID);

                    if(sim.simW.studyVillages)sim.simW.censedPigs.add(cp);

                    //if(ID == 62893)System.out.println("Pig 62893 ---------------------");

                    Boolean ana = false;
                    //only round 4 data
                    //if(!line.get(46).equals("1"))continue;

                    //if(line.get(2).equals("567") || line.get(2).equals("566") || line.get(2).equals("515") )
                    //if(1 == 1)//this is to analyze all the villages together
                    //int houseNum = Integer.parseInt(line.get(3));
                    //Household hh = sim.householdsGen.getHouseholdByshpId(houseNum);
                    //if(printOut)if(sim.extendedOutput)System.out.println (houseNum);


                    for(int round = 1; round < 5; round++)
                    {
                        //if(ID == 62893)System.out.println("round: " + round);
                        //if(ID == 62893)System.out.println(line);

                        cp.setRoundsCaptured(round, 0);

                        location = 7 + roundLenght * (round - 1);

                        if(!line.get(location).equals(""))cp.setRoundsCaptured(round, 1);

                        String vill = line.get(2);
                        cp.roundsVillages.set(round, vill);

                        cp.roundsHouseholdsSeroIds.set(round, Integer.parseInt(line.get(3)));


                        if(sim.simW.studyVillages)
                        {
                            int hhNum = Integer.parseInt(line.get(3));
                            Household hh = sim.householdsGen.getHouseholdByshpId(hhNum);
                            if(!hh.censedPigs.contains(cp))hh.censedPigs.add(cp);
                        }


                        //if(sim.extendedOutput)if(round == 1)System.out.println(cp.roundsHouseholdsSeroIds.get(round));
                        //if(sim.extendedOutput)if(round == 1)if(cp.roundsCaptured.get(round) == 1)System.out.println(line);

                        //if(round == 4 && cp.roundsVillages.get(i).equals(sim.villageNameNumber))statsCp++;

                        location = 8 + roundLenght * (round - 1);
                        if(cp.roundsCaptured.get(round) == 1)
                        {
                            age = (int)Math.round(sim.weeksInAMonth * ( Integer.parseInt(line.get(location)))  );
                            ageInMonths = Integer.parseInt(line.get(location));
                        }
                        else 
                        {
                            age = -100;
                            ageInMonths = -100;
                        }
                        cp.addAges(round, age);
                        cp.roundsAgesInMonths.set(round, ageInMonths);

                        location = 12 + roundLenght * (round - 1);
                        wbValue = line.get(location);
                        if(!wbValue.equals(""))cp.roundsNotMissing.set(round, 1);

                        //read the num of positive bands for this round
                        //-100 no data found
                        numBands =-100;
                        if(!wbValue.equals(""))
                        {
                            //if(round == 1)System.out.println (wbValue + " " + ID);

                            numBands = 0;

                            //GP50 band excluded
                            //location = 13 + (round - 1) * roundLenght;
                            //if(line.get(location).equals("1"))numBands++;

                            location = 14 + (round - 1) * roundLenght;
                            if(line.get(location).equals("1"))numBands++;

                            location = 15 + (round - 1) * roundLenght;
                            if(line.get(location).equals("1"))numBands++;

                            location = 16 + (round - 1) * roundLenght;
                            if(line.get(location).equals("1"))numBands++;

                            location = 17 + (round - 1) * roundLenght;
                            if(line.get(location).equals("1"))numBands++;

                            location = 18 + (round - 1) * roundLenght;
                            if(line.get(location).equals("1"))numBands++;

                            location = 19 + (round - 1) * roundLenght;
                            if(line.get(location).equals("1"))numBands++;
                        }

                        //if(ID == 62893)System.out.println("bands: " + numBands);

                        if(numBands > 0)cp.roundsSeroState.set(round, 1);
                        else if(numBands == 0)cp.roundsSeroState.set(round, 0);

                        cp.setBands(round, numBands);
                        cp.setWbValue(round, wbValue);
                        //if(ID == 62893)cp.printResume();
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

        //if(sim.extendedOutput)System.out.println(statsCp);

        //System.exit(0);
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

    //-----------------------------------------------
    public void createCohorts()
    {
        if(sim.extendedOutput)System.out.println("");
        if(sim.extendedOutput)System.out.println(sim.villageName + "---- Creating Seroincidence Cohort");

        if(sim.extendedOutput)System.out.println(sim.villageName + " Num starting censed Pigs: " + sim.censedPigsBag.size()); 

        int stats = 0;

        //-------------------------------------------------------------
        //Remove pigs with duplicate ID

        stats = 0;

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            for(int cc2 = 0; cc2 < sim.censedPigsBag.size(); cc2++)
            {
                CensedPig cp2 = (CensedPig)sim.censedPigsBag.get(cc2);

                if(cp == cp2)continue;

                if(cp.ID == cp2.ID)
                {
                    cp.excluded = true;
                    cp.excludedWhy = 2;
                    stats++;
                }
            }
        }

        if(sim.extendedOutput)System.out.println("-----------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num Pigs after removing pigs with duplicate ID: " + getNumNotExcludedPigs());
        if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs removed: " + stats);

        //-------------------------------------------------------------
        //Creating a correcte age time serie for each pig
        if(sim.extendedOutput)System.out.println("-----------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Creating a corrected time series for pigs ages");

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            if(cp.excluded)continue;

            int startI = -100;
            int startAge = -10;
            int startAgeInMonths = 0;

            Boolean start = false;

            for(int i = 1; i < 5; i++)
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
                    cp.roundsAgesCorrected.set(i, (startAge + (int)Math.round(startI * 4 * sim.weeksInAMonth)));
                    cp.roundsAgesCorrectedInMonths.set(i, (startAgeInMonths + (startI * 4)));
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
           for(int i = 1; i < 5; i++)
           {
           if(sim.extendedOutput)System.out.println("round: " + i + " " + cp.roundsAges.get(i) + " " + cp.roundsAgesCorrected.get(i));
           }
           }
           System.exit(0);
           */

        //---------------------------------------------------------------
        //creating the cohorts      -------------------------------------
        if(sim.extendedOutput)System.out.println("-----------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Creating seroincidence age-based cohorts");

        stats = 0;
        int statsCoho = 0;
        int numInTheCohort = 0;
        int numNotInTheCohort = 0;

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);
            if(cp.excluded)continue;

            int coho = 0;

            for(int i = 1; i < 5; i++)
            {
                if(cp.roundsAgesCorrected.get(i) < 0)
                {
                    cp.isInTheCohort.set(i,0);
                    numNotInTheCohort++;
                    continue;
                }

                if(cp.roundsAgesCorrectedInMonths.get(i) >= sim.cohortSeroIncAgesInMonths.get(0)
                        && cp.roundsAgesCorrectedInMonths.get(i) <= sim.cohortSeroIncAgesInMonths.get(1))
                {
                    cp.isInTheCohort.set(i, 1);
                }
                else
                {
                    cp.isInTheCohort.set(i, 0);
                }

                if(cp.isInTheCohort.get(i) == 1)
                {
                    cp.roundsCohortColumn = 1;
                    numInTheCohort++;
                }
                else
                {
                    numNotInTheCohort++;
                }

            }

        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " Num. Pigs in the seroincidence cohort: " + numInTheCohort);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num. Pigs not in the seroincidence cohort: " + numNotInTheCohort);


        //---------------------------------------------------------------
        //form the incidence chorts -------------------------------------
        if(sim.extendedOutput)System.out.println("-----------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Creating eligible observations");

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            if(cp.excluded || cp.roundsCohortColumn != 1)
            {
                for(int i = 1; i < 5; i++)
                {
                    cp.isEligible.set(i, -50);//-50 for pigs not in the cohorts
                }
                continue;
            }

            for(int i = 1; i < 5; i++)
            {
                if(cp.roundsBands.get(i) == -100)
                {
                    cp.isEligible.set(i, -100);//-100 for missing data
                    continue;
                }

                cp.isEligible.set(i, 1);//1 for observation eligible for seroincidence calculation

                if(i > 1)
                {
                    for(int j = 1; j < i; j++)
                    {
                        if(cp.roundsBands.get(j) > 0)
                        {
                            cp.isEligible.set(i, 0);//0 for observation not eligible for seroincidence calculations
                        }
                    }
                }
            }
        }

        getNumObservations();

        //System.exit(0);


    }

    //-----------------------------------------------
    public void calculatePrevalencesByAge()
    {
        double perseroPrevalencePiglets = 0.0;
        double perseroPrevalencePigletsNorm = 0.0;
        double perseroPrevalenceTransition = 0.0;
        double perseroPrevalenceTransitionNorm = 0.0;
        double perseroPrevalenceYoungPigs = 0.0;
        double perseroPrevalenceYoungPigsNorm = 0.0;
        double perseroPrevalenceAdultPigs = 0.0;
        double perseroPrevalenceAdultPigsNorm = 0.0;

        List<Double> listSeroPrevalencePiglets = new ArrayList<Double>();
        List<Double> listSeroPrevalencePigletsNorm = new ArrayList<Double>();
        List<Double> listSeroPrevalenceTransition = new ArrayList<Double>();
        List<Double> listSeroPrevalenceTransitionNorm = new ArrayList<Double>();
        List<Double> listSeroPrevalenceYoungPigs = new ArrayList<Double>();
        List<Double> listSeroPrevalenceYoungPigsNorm = new ArrayList<Double>();
        List<Double> listSeroPrevalenceAdultPigs = new ArrayList<Double>();
        List<Double> listSeroPrevalenceAdultPigsNorm = new ArrayList<Double>();

        for(int i = 0; i < 5; i++)
        {
            listSeroPrevalencePiglets.add(0.0);
            listSeroPrevalencePigletsNorm.add(0.0);
            listSeroPrevalenceTransition.add(0.0);
            listSeroPrevalenceTransitionNorm.add(0.0);
            listSeroPrevalenceYoungPigs.add(0.0);
            listSeroPrevalenceYoungPigsNorm.add(0.0);
            listSeroPrevalenceAdultPigs.add(0.0);
            listSeroPrevalenceAdultPigsNorm.add(0.0);

        }

        for(int i = 0; i < 1000; i++)
        {
            prevalenceByAge.add(0.0);
            prevalenceByAgeNorm.add(0.0);
        }

        int stats = 0;

        for(int i = 1; i < 5; i++)
        {
            //if(i == 1 || i == 2 || i == 3)continue;

            for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
            {
                CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

                if(!cp.roundsVillages.get(i).equals(sim.villageNameNumber))continue;

                int ageM = cp.roundsAgesCorrectedInMonths.get(i);
                //int ageM = cp.roundsAgesInMonths.get(i);

                if(ageM == -100)continue;

                //if(i == 2 || i == 3)continue;

                //if(sim.extendedOutput)System.out.println(ageM);


                //all the rounds toghter
                if(cp.roundsBands.get(i) != -100)
                {
                    prevalenceByAgeNorm.set(ageM, (prevalenceByAgeNorm.get(ageM) + 1.0));
                    if(cp.roundsBands.get(i) >  0)prevalenceByAge.set(ageM, (prevalenceByAge.get(ageM) + 1.0));
                    stats++;


                    if(ageM >= 2 && ageM <= 3)
                    {
                        perseroPrevalencePigletsNorm++;
                        if(cp.roundsBands.get(i) > 0)perseroPrevalencePiglets++;
                    }

                    if(ageM == 4)
                    {
                        perseroPrevalenceTransitionNorm++;
                        if(cp.roundsBands.get(i) > 0)perseroPrevalenceTransition++;
                    }

                    if(ageM >= 5 && ageM <= 9 && (i != 2 && i != 3))
                    {
                        perseroPrevalenceYoungPigsNorm++;
                        if(cp.roundsBands.get(i) > 0)perseroPrevalenceYoungPigs++;
                    }

                    if(ageM >= 10 && (i != 2 && i != 3))
                    {
                        perseroPrevalenceAdultPigsNorm++;
                        if(cp.roundsBands.get(i) > 0)perseroPrevalenceAdultPigs++;
                    }

                }

                //each round separately
                if(cp.roundsBands.get(i) != -100)
                {
                    if(ageM >= 2 && ageM <= 3)
                    {
                        listSeroPrevalencePigletsNorm.set(i, (listSeroPrevalencePigletsNorm.get(i) + 1.0));
                        if(cp.roundsBands.get(i) > 0)listSeroPrevalencePiglets.set(i, (listSeroPrevalencePiglets.get(i) + 1.0));
                    }

                    if(ageM == 4)
                    {
                        listSeroPrevalenceTransitionNorm.set(i, (listSeroPrevalenceTransitionNorm.get(i) + 1.0));
                        if(cp.roundsBands.get(i) > 0)listSeroPrevalenceTransition.set(i, (listSeroPrevalenceTransition.get(i) + 1.0));
                    }

                    if(ageM >= 5 && ageM <= 9 && (i != 2 && i != 3))
                    {
                        listSeroPrevalenceYoungPigsNorm.set(i, (listSeroPrevalenceYoungPigsNorm.get(i) + 1.0));
                        if(cp.roundsBands.get(i) > 0)listSeroPrevalenceYoungPigs.set(i, (listSeroPrevalenceYoungPigs.get(i) + 1.0));
                    }

                    if(ageM >= 10 && (i != 2 && i != 3))
                    {
                        listSeroPrevalenceAdultPigsNorm.set(i, (listSeroPrevalenceAdultPigsNorm.get(i) + 1.0));
                        if(cp.roundsBands.get(i) > 0)listSeroPrevalenceAdultPigs.set(i, (listSeroPrevalenceAdultPigs.get(i) + 1.0));
                    }

                }
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

        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + "---- Prevalence by pig age:");
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
        if(sim.extendedOutput)System.out.println("-------------------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + "---- Prevalence by pigs age segments in eahc round:");

        for(int i = 1; i < 5; i++)
        {
            tmp = "---- Round: " + i + " ----";
            if(sim.extendedOutput)System.out.println(sim.villageName + tmp);

            tmp = " Observed Piglets Seroprevalence with the seroincidence cohorts periodicity (7 <= age <= 14 weeks): ";
            if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  listSeroPrevalencePiglets.get(i)/(double)listSeroPrevalencePigletsNorm.get(i));
            //if(sim.extendedOutput)System.out.println(listSeroPrevalencePiglets.get(i) + " " + listSeroPrevalencePigletsNorm.get(i));

            tmp = " Observed Pigs Seroprevalence in the transition age segment with the seroincidence cohorts periodicity (15 <= age <= 20): ";
            if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  listSeroPrevalenceTransition.get(i)/(double)listSeroPrevalenceTransitionNorm.get(i));
            //if(sim.extendedOutput)System.out.println(listSeroPrevalenceTransition.get(i) + " " + listSeroPrevalenceTransitionNorm.get(i));

            tmp = " Observed Young Pigs Seroprevalence with the seroincidence cohorts periodicity (21 <= age <= 37 weeks): ";
            if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  listSeroPrevalenceYoungPigs.get(i)/(double)listSeroPrevalenceYoungPigsNorm.get(i));
            //if(sim.extendedOutput)System.out.println(listSeroPrevalenceYoungPigs.get(i)+ " " + listSeroPrevalenceYoungPigsNorm.get(i));

            tmp = " Observed Adult Pigs Seroprevalence with the seroincidence cohorts periodicity (38 <= age weeks): ";
            if(sim.extendedOutput)System.out.println(sim.villageName + tmp +  listSeroPrevalenceAdultPigs.get(i)/(double)listSeroPrevalenceAdultPigsNorm.get(i));
            //if(sim.extendedOutput)System.out.println(listSeroPrevalenceAdultPigs.get(i) + " " + listSeroPrevalenceAdultPigsNorm.get(i));
        }

        //System.exit(0);

    }

    //-----------------------------------------------
    public void calculateIncidencesTTEMP()
    {
        if(sim.extendedOutput)System.out.println("");
        if(sim.extendedOutput)System.out.println(sim.villageName + "---- Calculating Seroincidence");

        int pigsPopPrevVillage = 0;
        int pigsPositivePrevVillage = 0;

        int overallPigsPopPrevVillage = 0;
        int overallPigsPositivePrevVillage = 0;

        int pigletsPopPrevVillage = 0;
        int pigletsPositivePrevVillage = 0;

        int pigsPopIncVillage = 0;
        int pigsPositiveIncVillage = 0;

        int pigsPopPrevAll = 0;
        int pigsPositivePrevAll = 0;

        int overallPigsPopPrevAll = 0;
        int overallPigsPositivePrevAll = 0;

        int pigsPopIncAll = 0;
        int pigsPositiveIncAll = 0;

        int pigletsPopPrevAll = 0;
        int pigletsPositivePrevAll = 0;

        for(int i = 0; i < 5; i++)
        {
            sim.obsSeroIncidencePigsRounds.add(0.0);
            sim.obsSeroPrevalencePigsRounds.add(0.0);
            sim.obsOverallSeroPrevalencePigsRounds.add(0.0);
            sim.obsSeroPrevalencePigletsRounds.add(0.0);

            seroIncidencePigsRoundsAll.add(0.0);
            seroPrevalencePigsRoundsAll.add(0.0);
            overallSeroPrevalencePigsRoundsAll.add(0.0);
            seroPrevalencePigletsRoundsAll.add(0.0);
        }


        for(int i = 1; i < 5; i++)
        {
            pigsPopIncAll = 0;
            pigsPositiveIncAll = 0;

            pigsPopPrevAll = 0;
            pigsPositivePrevAll = 0;

            overallPigsPopPrevAll = 0;
            overallPigsPositivePrevAll = 0;

            pigletsPopPrevAll = 0;
            pigletsPositivePrevAll = 0;

            pigsPopIncVillage = 0;
            pigsPositiveIncVillage = 0;

            pigsPopPrevVillage = 0;
            pigsPositivePrevVillage = 0;

            overallPigsPopPrevVillage = 0;
            overallPigsPositivePrevVillage = 0;

            pigletsPopPrevVillage = 0;
            pigletsPositivePrevVillage = 0;

            for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
            {
                CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

                if(cp.excluded)continue;

                //if(cp.isEligible.get(i) != 1 && cp.isEligible.get(i) != 0)continue;

                if(cp.roundsVillages.get(i).equals(sim.villageNameNumber))
                {

                    if(cp.roundsCaptured.get(i) != 1)continue;

                    if(cp.isEligible.get(i) == 1)
                    {
                        pigsPopIncVillage++;
                        if(cp.roundsBands.get(i) > 0)pigsPositiveIncVillage++;
                    }

                    //here it consider the seroprevalence of pigs only in the age 
                    //group age > 4 months
                    if(cp.roundsBands.get(i) != -100 
                            && cp.roundsCaptured.get(i) == 1
                            && cp.roundsAgesCorrectedInMonths.get(i) > sim.cohortSeroIncAgesInMonths.get(1)
                      )
                    {
                        pigsPopPrevVillage++;
                        if(cp.roundsBands.get(i) >  0)pigsPositivePrevVillage++;
                    }

                    //System.out.println(sim.cohortSeroIncAges.get(1) + " " + sim.cohortSeroIncAgesInMonths.get(0));

                    if(cp.roundsBands.get(i) != -100)
                    {
                        overallPigsPopPrevVillage++;
                        if(cp.roundsBands.get(i) >  0)overallPigsPositivePrevVillage++;
                    }

                    //here it consider the seroprevalence of pigs only in the age 
                    //group age > 1.5 months and  age < 4 months
                    if(cp.roundsBands.get(i) != -100 
                            && cp.roundsCaptured.get(i) == 1
                            && cp.roundsAgesCorrectedInMonths.get(i) >= sim.cohortSeroIncAgesInMonths.get(0)
                            && cp.roundsAgesCorrectedInMonths.get(i) <= sim.cohortSeroIncAgesInMonths.get(1)
                      )
                    {
                        pigletsPopPrevVillage++;
                        if(cp.roundsBands.get(i) >  0)pigletsPositivePrevVillage++;
                    }

                }


                if(cp.isEligible.get(i) == 1)
                {
                    pigsPopIncAll++;
                    if(cp.roundsBands.get(i) > 0)pigsPositiveIncAll++;
                }

                if(cp.roundsBands.get(i) != -100 
                        && cp.roundsCaptured.get(i) == 1
                        && cp.roundsAgesCorrectedInMonths.get(i) > sim.cohortSeroIncAgesInMonths.get(1)
                  )
                {
                    pigsPopPrevAll++;
                    if(cp.roundsBands.get(i) >  0)pigsPositivePrevAll++;
                }

                if(cp.roundsBands.get(i) != -100)
                {
                    overallPigsPopPrevAll++;
                    if(cp.roundsBands.get(i) >  0)overallPigsPositivePrevAll++;
                }

                if(cp.roundsBands.get(i) != -100 
                        && cp.roundsCaptured.get(i) == 1
                        && cp.roundsAgesCorrectedInMonths.get(i) >= sim.cohortSeroIncAgesInMonths.get(0)
                        && cp.roundsAgesCorrectedInMonths.get(i) <= sim.cohortSeroIncAgesInMonths.get(1)
                  )
                {
                    pigletsPopPrevAll++;
                    if(cp.roundsBands.get(i) >  0)pigletsPositivePrevAll++;
                }
            }

            //village prevs
            sim.obsSeroPrevalencePigletsRounds.set(i, (double)pigletsPositivePrevVillage/(double)pigletsPopPrevVillage);

            sim.obsSeroPrevalencePigsRounds.set(i, (double)pigsPositivePrevVillage/(double)pigsPopPrevVillage);

            sim.obsOverallSeroPrevalencePigsRounds.set(i, (double)overallPigsPositivePrevVillage/(double)overallPigsPopPrevVillage);

            sim.obsSeroIncidencePigsRounds.set(i, (double)pigsPositiveIncVillage/(double)pigsPopIncVillage);


            //entire dataset prevs
            seroPrevalencePigletsRoundsAll.set(i, (double)pigletsPositivePrevAll/(double)pigletsPopPrevAll);

            seroPrevalencePigsRoundsAll.set(i, (double)pigsPositivePrevAll/(double)pigsPopPrevAll);

            overallSeroPrevalencePigsRoundsAll.set(i, (double)overallPigsPositivePrevAll/(double)overallPigsPopPrevAll);

            seroIncidencePigsRoundsAll.set(i, (double)pigsPositiveIncAll/(double)pigsPopIncAll);

            if(sim.extendedOutput)System.out.println("---- village results ---------");
            if(sim.extendedOutput)System.out.println("round: " + i);
            if(sim.extendedOutput)System.out.println("Num. positive piglets prevalence: " + pigletsPositivePrevVillage + " Num.  piglets population: " + pigletsPopPrevVillage);
            if(sim.extendedOutput)System.out.println("Num. positive pigs prevalence: " + pigsPositivePrevVillage + " Num.  pigs population: " + pigsPopPrevVillage);
            if(sim.extendedOutput)System.out.println("Num. positive incidence: " + pigsPositiveIncVillage + " Num. incidence population: " + pigsPopIncVillage);
        }


        if(sim.extendedOutput)System.out.println(" ");
        for(int i = 1; i < 5; i++)
        {
            if(sim.extendedOutput)System.out.println("---- Round " + i + " ----");
            if(sim.extendedOutput)System.out.println(sim.villageName + " Piglets (age < 4 months) SeroPrevalence: " +  sim.obsSeroPrevalencePigletsRounds.get(i));
            if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs (4 > age months) SeroPrevalence: " +  sim.obsSeroPrevalencePigsRounds.get(i));
            if(sim.extendedOutput)System.out.println(sim.villageName + " Overall Pigs SeroPrevalence : " +  sim.obsOverallSeroPrevalencePigsRounds.get(i));
            if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs SeroIncidence: " +  sim.obsSeroIncidencePigsRounds.get(i));

        }


        //System.exit(0);

    }


    //-----------------------------------------------
    public int getNumNotExcludedPigs()
    {
        int num = 0;

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);
            if(!cp.excluded)num++;

        }

        return num;
    }

    //-----------------------------------------------
    public void getNumObservations()
    {
        int stats = 0;
        int statsEligible = 0;
        int statsNoEligible = 0;
        int statsMissing = 0;
        int statsPigs = 0;

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            if(cp.excluded)continue;

            statsPigs++;

            for(int i = 1; i < 5; i++)
            {
                stats++;

                if(cp.isEligible.get(i) == 1)statsEligible++;
                if(cp.isEligible.get(i) == 0)statsNoEligible++;
                if(cp.isEligible.get(i) == -100)statsMissing++;
            }

        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " Tot. num of unique pigs: " + statsPigs);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Tot. num of oservations: " + stats);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Tot. num of eligible observations: " + statsEligible);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Tot. num of no eligible observations: " + statsNoEligible);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Tot. num of missing observations: " + statsMissing);
    }


    //===============================================
    public void readMissingSeroData()
    {
        Boolean printOut = false;

        String inputFile = "./inputData/populationsData/TTEMP/TTEMP_PorcinoRound1_Missing.xlsx";
        String sheetName = "Resultado western blot";

        if(sim.extendedOutput)System.out.println ("");
        if(sim.extendedOutput)System.out.println (sim.villageNameNumber + " Reading Missing pigs serology data ----");
        if(sim.extendedOutput)System.out.println ("---- Data input file: " + inputFile);

        //can be currentVillage or all to analyze the serology data of current 
        //village or all the villages together
        String whatAna = "currentVillage";

        List<Integer> pigsIds = new ArrayList<Integer>();
        List<Integer> numBandsList = new ArrayList<Integer>();

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
                if(statsRows == 0)continue;
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

                //if(sim.extendedOutput)System.out.println (line);
                pigsIds.add(Integer.parseInt(line.get(2)));

                int numBands = 0;

                //GP50 band excluded
                //if(line.get(5).equals("1"))numBands++;

                if(line.get(6).equals("1"))numBands++;
                if(line.get(7).equals("1"))numBands++;
                if(line.get(8).equals("1"))numBands++;
                if(line.get(9).equals("1"))numBands++;
                if(line.get(10).equals("1"))numBands++;
                if(line.get(11).equals("1"))numBands++;

                numBandsList.add(numBands);

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


        int stats = 0;
        int statsNotFound = 0;
        int statsFoundWithData = 0;

        for(int i = 0; i < pigsIds.size(); i++)
        {
            int IDnew = pigsIds.get(i);
            int numBands = numBandsList.get(i);

            Boolean found = false;
            stats = 0;

            for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
            {
                CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

                if(cp.ID == IDnew)
                {
                    found = true;
                    if(cp.roundsBands.get(1) >= 0)
                    {
                        statsFoundWithData++;
                        if(sim.extendedOutput)System.out.println ("Found with data ----");
                        if(sim.extendedOutput)System.out.println ("Id: " + IDnew);
                        if(sim.extendedOutput)System.out.println ("Old num bands: " + cp.roundsBands.get(1));
                        if(sim.extendedOutput)System.out.println ("New num bands: " + numBands);
                    }
                    else
                    {
                        //if(sim.extendedOutput)System.out.println ("Setting new data Id: " + IDnew);
                        cp.roundsBands.set(1, numBands);
                        cp.roundsCaptured.set(1, 1);
                    }

                    break;

                    //if(sim.extendedOutput)System.out.println ("new pig. ID: " + IDnew);
                }

            }

            if(!found)
            {
                statsNotFound++;
                //if(sim.extendedOutput)System.out.println ("new pig. ID: " + IDnew);
                //CensedPig cp1 = getCpById(IDnew);
                //if(cp1 != null)cp1.printResume();
                //else if(sim.extendedOutput)System.out.println ("cp1 is null");
            }


        }

        if(sim.extendedOutput)System.out.println ("Num new data: " + pigsIds.size());
        if(sim.extendedOutput)System.out.println ("Num not found: " + statsNotFound);
        if(sim.extendedOutput)System.out.println ("Num found with data: " + statsFoundWithData);


    }


    //====================================================
    public CensedPig getCpById(int targetID)
    {
        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            if(cp.ID == targetID)return cp;

        }
        return null;

    }

    //====================================================
    //Read the Pigs file ---------------------------
    public void getPigsLinesFromFileTTEMP_BrazoComunitario()
    {
        if(sim.extendedOutput)System.out.println ("---------------------------------");
        if(sim.extendedOutput)System.out.println ("---- Reading the pigs serology data from Brazo Comunitario ----");
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/populationsData/TTEMP/180619R01OA_Cerdos_BrazoComunitario.xlsx";
        sheetName = "Data_final";
        if(sim.extendedOutput)System.out.println ("Survey input file: " + inputFile);

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

                if(statsRows > 0)processPigLineTTEMP_BrazoComunitario(line);
                else if(sim.extendedOutput)System.out.println(sim.villageName + " line not processed");

            }

            if(sim.extendedOutput)System.out.println(sim.villageName + "num line processed in the pig serology file: " + statsRows);

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

        if(sim.extendedOutput)System.out.println(sim.villageName + "pigs: " + sim.pigsBag.size() + " Pigs generated");

        if(sim.extendedOutput)System.out.println("----------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " lines processed: " + statsLineProcessed);
        if(sim.extendedOutput)System.out.println("Num lines processed for villages R01: " + (double)statsVillaProcessed/(double)7);
        if(sim.extendedOutput)System.out.println("Num lines R01 (trials 1-7) after house  processed: " + (double)statsHouse/(double)7);

        //System.exit(0);

    }

    //====================================================
    public void processPigLineTTEMP_BrazoComunitario(List<String> line)
    {
        statsLineProcessed++;

        Boolean localPrintOut = false;
        int statsSero = 0;

        int statsPigs = 0;
        int statsPigsTot = 0;

        int indexT = 6;

        int roundLenght = 23;

        int location = 0;

        int numBands = 0;

        int age = 0;
        int ageInMonths = 0;

        int censed = 0;

        String wbValue = "";

        //System.out.println (housesLinesMap.get(91));

        if(localPrintOut)System.out.println ("=====================================");
        int ID = Integer.parseInt(line.get(0));
        if(localPrintOut)System.out.println ("pig ID: " + ID);

        //create the censedPig 
        CensedPig cp = new CensedPig(state, ID);

        if(sim.simW.studyVillages)sim.simW.censedPigs.add(cp);

        //this is not the ordinary TTEMP censed pigs bag
        sim.censedPigsBag.remove(cp);
        sim.censedPigsBagTTEMPBC.add(cp);

        //read the intervention group
        List<String> rm = new ArrayList<String>(); 
        rm.add(".");
        rm.add(line.get(3));
        rm.add(line.get(4));
        rm.add(line.get(5));
        rm.add(line.get(6));
        rm.add(line.get(7));
        rm.add(line.get(8));
        rm.add(line.get(9));
        rm.add(line.get(10));

        cp.roundsModels = rm;

        if(!line.get(2).equals("."))cp.roundsCohortColumn = Integer.parseInt(line.get(2));

        for(int round = 1; round < 5; round++)
        {
            cp.setRoundsCaptured(round, 0);

            //if(round != 1)location = 12 + (round - 1) * roundLenght;
            //else location = 11;
            location = 12 + (round - 1) * roundLenght;

            String vill = line.get(location);
            cp.roundsVillages.set(round, vill);

            if(vill.equals("."))continue;
            if(!vill.equals(sim.villageNameNumber))continue;

            location = 13 + (round - 1) * roundLenght;
            String hhS = line.get(location);

            int hhNum = 0;
            if(round == 1 || round == 2)
            {
                //if(!hhS.equals("."))hhNum = Integer.parseInt(hhS);
                hhNum = Integer.parseInt(hhS);
            }
            else  if(round == 3 && hhS.length() > 3)
            {
                String lastThreeDigits = hhS.substring(hhS.length() - 3);
                hhNum = Integer.parseInt(lastThreeDigits);

            }
            else  if(round == 3 && hhS.length() <= 3)
            {
                hhNum = Integer.parseInt(hhS);

            }

            else  if(round == 4)
            {
                hhNum = Integer.parseInt(hhS);

            }
            //System.out.println (hhNum);

            cp.roundsHouseholdsSeroIds.set(round, hhNum);

            if(sim.simW.studyVillages)
            {
                Household hh = sim.householdsGen.getHouseholdByshpId(hhNum);
                hh.censedPigs.add(cp);
                if(!hh.censedPigs.contains(cp))hh.censedPigs.add(cp);
            }

            statsVillaProcessed++;

            cp.setRoundsCaptured(round, 1);

            if(round == 2)if(localPrintOut)System.out.println ("--------------");
            if(round == 2)if(localPrintOut)System.out.println ("round: " + round);
            if(round == 2)if(localPrintOut)System.out.println (location);
            if(round == 2)if(localPrintOut)System.out.println ("Village: " + line.get(location));

            //System.out.println (line.get(location));

            //if(Integer.parseInt(line.get(location)) != hh.shpId)continue;

            //if(round != 1)location = 14 + (round - 1) * roundLenght + 1;
            //else location = 14;

            location = 15 + (round - 1) * roundLenght + 1;

            age = (int)Math.round(sim.weeksInAMonth * ( Integer.parseInt(line.get(location)))  );
            ageInMonths = Integer.parseInt(line.get(location));
            cp.addAges(round, age);
            cp.roundsAgesInMonths.set(round, ageInMonths);

            wbValue = ".";
            location = 26 + (round - 1) * roundLenght;
            //if(round != 8)wbValue = line.get(location);

            if(!wbValue.equals("."))cp.roundsNotMissing.set(round, 1);

            //read the num of positive bands for this round
            numBands = -100;
            if(!wbValue.equals("."))
            {
                numBands = 0;

                //GP50 band excluded
                //location = 27 + (round - 1) * roundLenght;
                //if(line.get(location).equals("1"))numBands++;

                location = 28 + (round - 1) * roundLenght;
                if(line.get(location).equals("1"))numBands++;

                location = 29 + (round - 1) * roundLenght;
                if(line.get(location).equals("1"))numBands++;

                location = 30 + (round - 1) * roundLenght;
                if(line.get(location).equals("1"))numBands++;

                location = 31 + (round - 1) * roundLenght;
                if(line.get(location).equals("1"))numBands++;

                location = 32 + (round - 1) * roundLenght;
                if(line.get(location).equals("1"))numBands++;

                location = 33 + (round - 1) * roundLenght;
                if(line.get(location).equals("1"))numBands++;
            }

            //if(numBands == 0)System.out.println(numBands);

            //hh.targetNumOfPigs = statsPigs;
            //System.out.println("HH: " + hh.shpId + " target pigs: " + statsPigs);

            //System.out.println (numBands + " " + round);

            
            if(numBands > 0)cp.roundsSeroState.set(round, 1);
            else if(numBands == 0)cp.roundsSeroState.set(round, 0);

            cp.setBands(round, numBands);
            cp.setWbValue(round, wbValue);

            //collect baseline data ----------------------
            if(round == 1 && vill.equals(sim.villageNameNumber))
            {
                if(localPrintOut)System.out.println ("round: " + round);
                //if(round != 1)location = 12 + (round - 1) * roundLenght + 1;
                //else location = 12;
                location = 13;

                //if(round == 2)if(localPrintOut)System.out.println ("House: " + line.get(location) + " pig: " + ID);

                if(line.get(location).equals("."))continue;
                int hhShpId = Integer.parseInt(line.get(location));

                Household hh = sim.householdsGen.getHouseholdByshpId(hhShpId);

                if(hh == null)
                {
                    if(sim.extendedOutput)System.out.println ("Household with shpId: " + hhShpId + "no found");
                    //System.exit(0);
                }

                if(localPrintOut)System.out.println ("round: " + round);


                hh.pigOwner = true;

                statsPigsTot++;
                //hh.targetNumOfPigs++;

                /*
                Pig pig = new Pig(state, hh, false, false);

                double rand = state.random.nextDouble();
                if(rand < sim.importPrev)
                {
                    sim.pigsGen.assignPigCysts(pig);
                }

                if(localPrintOut)System.out.println ("line: " + line);

                pig.age = age;

                pig.R01ID = ID;

                sim.village.R01InterventionArm = Integer.parseInt(line.get(3));
                if(localPrintOut)System.out.println (sim.village.R01InterventionArm);
                //System.exit(0);

                //gender
                location = 16 + (round - 1) * roundLenght;
                if(line.get(location).equals("0"))pig.gender = "female";
                else pig.gender = "male";
                if(printOut)if(sim.extendedOutput)System.out.println ("New Pig gender: " + pig.gender);

                location = 23 + (round - 1) * roundLenght;
                if(!line.get(location).equals(sim.villageNameNumber))pig.imported = true;

                //if(hh.corralUse.equals("never"))pig.corraled = "never";
                //else if(hh.corralUse.equals("sometimes"))pig.corraled = "sometimes";
                //else if(hh.corralUse.equals("always"))pig.corraled = "always";

                pig.numBands = numBands;
                if(numBands > 0)
                {
                    pig.seropositive = true;
                    statsSero++;
                }
                */
            }

            //if(round == 2)System.out.println("pig: " + ID + " round: " + round + " num bands: " + numBands); 
        }


        //System.exit(0);



    }


    //====================================================
    public void getStats_BrazoComunitario()
    {
        List<Double> numPigsRound = new ArrayList<Double>();

        List<Integer> hhIdsSero = new ArrayList<Integer>();
        HashMap<Integer, List<Integer>> mapRoundHhs = new HashMap<Integer, List<Integer>>();

        for(int i = 0; i < 5; i++)
        {
            numPigsRound.add(0.0);

            mapRoundHhs.put(i, new ArrayList<Integer>());
        }

        for(int cc = 0; cc < sim.censedPigsBagTTEMPBC.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBagTTEMPBC.get(cc);

            //System.out.println ("Cp Pig id: " + cp.ID);

            for(int round = 1; round < 5; round++)
            {
                hhIdsSero = mapRoundHhs.get(round);

                if(cp.roundsHouseholdsSeroIds.get(round) == -100)continue;

                if(cp.roundsCaptured.get(round) != 1)continue;

                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;

                if(!hhIdsSero.contains(cp.roundsHouseholdsSeroIds.get(round)))
                {
                    hhIdsSero.add(cp.roundsHouseholdsSeroIds.get(round));
                }

                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;
                if(cp.roundsCaptured.get(round) == 1)
                {
                    numPigsRound.set(round, (numPigsRound.get(round) + 1));
                    //System.out.println ("Cp captured");
                }

            }

        }


        int numPigsShp = 0;
        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);
            numPigsShp = numPigsShp + hh.targetNumOfPigs;
        }

        if(sim.extendedOutput)
        {
            System.out.println ("----------------------------------------------------------------");
            System.out.println ("----------------------------------------------------------------");
            System.out.println ("---- Comparison of household survey and Brazo Comunitario serology data ----");

            System.out.println (" ");
            System.out.println ("number of households survey: " + sim.householdsBag.size());
            System.out.println ("Num pigs survey: " + numPigsShp);

            for(int round = 1; round < 5; round++)
            {
                hhIdsSero = mapRoundHhs.get(round);

                int statsHhSero = 0;
                int statsHhShp = 0;

                for(int i = 0; i < sim.householdsBag.size(); i++)
                {
                    Household hh = (Household)sim.householdsBag.get(i);
                    numPigsShp = numPigsShp + hh.targetNumOfPigs;

                    if(!hhIdsSero.contains(hh.shpId))
                    {
                        statsHhSero++;
                        continue;
                    }
                }


                for(int j = 0; j < hhIdsSero.size(); j++)
                {
                    int tmp = hhIdsSero.get(j);

                    Boolean gotIt = false;

                    for(int i = 0; i < sim.householdsBag.size(); i++)
                    {
                        Household hh = (Household)sim.householdsBag.get(i);

                        if(hh.shpId == tmp)
                        {
                            gotIt = true;
                            break;
                        }
                    }

                    if(!gotIt)statsHhShp++;
                }

                System.out.println ("---- Round: " + round + " --------");
                System.out.println ("number of households in survey not in sero file: " + statsHhSero);
                System.out.println ("number of households in sero not in survey file: " + statsHhShp);

                System.out.println (" ");

                System.out.println ("number of households sero: " + mapRoundHhs.get(round).size());
                System.out.println ("Num pigs in this round serology: " + numPigsRound.get(round));


            }

            System.out.println ("----------------------------------------------------------------");
            System.out.println (" ");
        }



    }

    //====================================================
    public void printSero_TTEMP()
    {
        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            //if(!cp.roundsVillages.get(round).equals("566") &&
            //   !cp.roundsVillages.get(round).equals("515"))continue;



        }
    }

    //====================================================
    public void getStats_TTEMP()
    {
        List<Double> numPigsRound = new ArrayList<Double>();

        List<Integer> hhIdsSero = new ArrayList<Integer>();
        HashMap<Integer, List<Integer>> mapRoundHhs = new HashMap<Integer, List<Integer>>();

        for(int i = 0; i < 5; i++)
        {
            numPigsRound.add(0.0);

            mapRoundHhs.put(i, new ArrayList<Integer>());
        }

        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            //if(cp.ID == 62893)cp.printResume();

            //System.out.println ("Cp Pig id: " + cp.ID);

            for(int round = 1; round < 5; round++)
            {
                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;
                if(cp.roundsHouseholdsSeroIds.get(round) == -100)continue;
                if(cp.roundsCaptured.get(round) != 1)continue;

                hhIdsSero = mapRoundHhs.get(round);

                if(!hhIdsSero.contains(cp.roundsHouseholdsSeroIds.get(round)))
                {
                    hhIdsSero.add(cp.roundsHouseholdsSeroIds.get(round));
                }

                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;
                if(cp.roundsCaptured.get(round) == 1)
                {
                    numPigsRound.set(round, (numPigsRound.get(round) + 1));
                    //System.out.println ("Cp captured");
                }

            }

        }


        int numPigsShp = 0;
        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);
            numPigsShp = numPigsShp + hh.targetNumOfPigs;
        }

        if(sim.extendedOutput)
        {
            System.out.println ("----------------------------------------------------------------");
            System.out.println ("----------------------------------------------------------------");
            System.out.println ("---- Comparison of household survey and TTEMP serology data ----");

            System.out.println (" ");
            System.out.println ("number of households survey: " + sim.householdsBag.size());
            System.out.println ("Num pigs survey: " + numPigsShp);

            for(int round = 1; round < 5; round++)
            {
                hhIdsSero = mapRoundHhs.get(round);

                int statsHhSero = 0;
                int statsHhShp = 0;

                for(int i = 0; i < sim.householdsBag.size(); i++)
                {
                    Household hh = (Household)sim.householdsBag.get(i);
                    numPigsShp = numPigsShp + hh.targetNumOfPigs;

                    if(!hhIdsSero.contains(hh.shpId))
                    {
                        statsHhSero++;
                        continue;
                    }
                }


                for(int j = 0; j < hhIdsSero.size(); j++)
                {
                    int tmp = hhIdsSero.get(j);

                    Boolean gotIt = false;

                    for(int i = 0; i < sim.householdsBag.size(); i++)
                    {
                        Household hh = (Household)sim.householdsBag.get(i);

                        if(hh.shpId == tmp)
                        {
                            gotIt = true;
                            break;
                        }
                    }

                    if(!gotIt)statsHhShp++;
                }

                System.out.println ("---- Round: " + round + " --------");
                System.out.println ("number of households in survey not in sero file: " + statsHhSero);
                System.out.println ("number of households in sero not in survey file: " + statsHhShp);

                System.out.println (" ");

                System.out.println ("number of households sero: " + mapRoundHhs.get(round).size());
                System.out.println ("Num pigs in this round serology: " + numPigsRound.get(round));


            }

            System.out.println ("----------------------------------------------------------------");
            System.out.println (" ");
        }

        //System.exit(0);

    }

    //====================================================
    public void getStats_TTEMP_BrazoComunitario()
    {
        List<Double> numPigsRoundBC = new ArrayList<Double>();
        List<Double> numPigsRoundTTEMP = new ArrayList<Double>();

        List<Integer> hhIdsSero = new ArrayList<Integer>();
        List<Integer> hhIdsSeroBC = new ArrayList<Integer>();
        List<Integer> hhIdsSeroTTEMP = new ArrayList<Integer>();

        HashMap<Integer, List<Integer>> mapRoundHhsBC = new HashMap<Integer, List<Integer>>();
        HashMap<Integer, List<Integer>> mapRoundHhsTTEMP = new HashMap<Integer, List<Integer>>();

        for(int i = 0; i < 5; i++)
        {
            numPigsRoundBC.add(0.0);
            numPigsRoundTTEMP.add(0.0);

            mapRoundHhsBC.put(i, new ArrayList<Integer>());
            mapRoundHhsTTEMP.put(i, new ArrayList<Integer>());
        }

        //BC
        for(int cc = 0; cc < sim.censedPigsBagTTEMPBC.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBagTTEMPBC.get(cc);

            //System.out.println ("-----------------------");
            //System.out.println ("Cp Pig id: " + cp.ID);

            for(int round = 1; round < 5; round++)
            {
                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;
                if(cp.roundsHouseholdsSeroIds.get(round) == -100)continue;
                if(cp.roundsCaptured.get(round) != 1)continue;
                //System.out.println ("-----------------------");
                //System.out.println ("---- round: " + round);
                //System.out.println ("Cp Pig house id: " + cp.roundsHouseholdsSeroIds.get(round));
                hhIdsSero = mapRoundHhsBC.get(round);
                //System.out.println ("num houses: " + hhIdsSero.size());
                //System.out.println (hhIdsSero);


                if(!hhIdsSero.contains(cp.roundsHouseholdsSeroIds.get(round)))
                {
                    hhIdsSero.add(cp.roundsHouseholdsSeroIds.get(round));
                    //System.out.println ("not contains");
                }

                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;
                if(cp.roundsCaptured.get(round) == 1)
                {
                    numPigsRoundBC.set(round, (numPigsRoundBC.get(round) + 1));
                    //System.out.println ("Cp captured");
                }
            }
        }

        //TTEMP
        for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
        {
            CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

            //System.out.println ("Cp Pig id: " + cp.ID);

            for(int round = 1; round < 5; round++)
            {

                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;

                if(cp.roundsHouseholdsSeroIds.get(round) == -100)continue;

                if(cp.roundsCaptured.get(round) != 1)continue;

                //System.out.println ("-----------------------");
                //System.out.println ("---- round: " + round);
                //System.out.println ("Cp Pig house id: " + cp.roundsHouseholdsSeroIds.get(round));

                hhIdsSero = mapRoundHhsTTEMP.get(round);

                //System.out.println ("num houses: " + hhIdsSero.size());
                //System.out.println (hhIdsSero);

                if(!hhIdsSero.contains(cp.roundsHouseholdsSeroIds.get(round)))
                {
                    hhIdsSero.add(cp.roundsHouseholdsSeroIds.get(round));
                }

                if(!cp.roundsVillages.get(round).equals(sim.villageNameNumber))continue;
                if(cp.roundsCaptured.get(round) == 1)
                {
                    numPigsRoundTTEMP.set(round, (numPigsRoundTTEMP.get(round) + 1));
                    //System.out.println ("Cp captured");
                }
            }
        }


        if(sim.extendedOutput)
        {
            System.out.println ("----------------------------------------------------------------");
            System.out.println ("----------------------------------------------------------------");
            System.out.println ("---- Comparison of household TTEMP serology and Brazo Comunitario data ----");

            for(int round = 1; round < 5; round++)
            {
                hhIdsSeroTTEMP = mapRoundHhsTTEMP.get(round);
                hhIdsSeroBC = mapRoundHhsBC.get(round);

                int statsHhBC = 0;
                int statsHhTTEMP = 0;

                for(int j = 0; j < hhIdsSeroBC.size(); j++)
                {
                    int tmpBC = hhIdsSeroBC.get(j);

                    if(tmpBC == -100)continue;

                    Boolean gotIt = false;

                    for(int i = 0; i < hhIdsSeroTTEMP.size(); i++)
                    {
                        int tmpTTEMP = hhIdsSeroTTEMP.get(i);

                        if(tmpTTEMP == -100)continue;

                        if(tmpBC == tmpTTEMP)
                        {
                            gotIt = true;
                            break;
                        }
                    }

                    if(!gotIt)statsHhBC++;
                }

                for(int j = 0; j < hhIdsSeroTTEMP.size(); j++)
                {
                    int tmpTTEMP = hhIdsSeroTTEMP.get(j);

                    if(tmpTTEMP == -100)continue;

                    Boolean gotIt = false;

                    for(int i = 0; i < hhIdsSeroBC.size(); i++)
                    {
                        int tmpBC = hhIdsSeroBC.get(i);

                        if(tmpBC == -100)continue;

                        if(tmpBC == tmpTTEMP)
                        {
                            gotIt = true;
                            break;
                        }
                    }

                    if(!gotIt)statsHhTTEMP++;
                }



                System.out.println ("---- Round: " + round + " --------");
                System.out.println ("number of households in BC not in TTEMP file: " + statsHhBC);
                System.out.println ("number of households in TTEMP not in BC file: " + statsHhTTEMP);

                System.out.println (" ");

                System.out.println ("number of households BC: " + mapRoundHhsBC.get(round).size());
                System.out.println ("number of households TTEMP: " + mapRoundHhsTTEMP.get(round).size());
                System.out.println (" ");
                System.out.println ("Num pigs in this round BC: " + numPigsRoundBC.get(round));
                System.out.println ("Num pigs in this round TTEMP: " + numPigsRoundTTEMP.get(round));


            }

            System.out.println ("----------------------------------------------------------------");
            System.out.println (" ");
        }



    }

    //-----------------------------------------------
    public void calculateIncidencesTTEMPJoined()
    {
        if(sim.extendedOutput)System.out.println("");
        if(sim.extendedOutput)System.out.println(sim.villageName + "---- Calculating Seroincidence");

        int pigsPopPrevVillage = 0;
        int pigsPositivePrevVillage = 0;

        int overallPigsPopPrevVillage = 0;
        int overallPigsPositivePrevVillage = 0;

        int pigletsPopPrevVillage = 0;
        int pigletsPositivePrevVillage = 0;

        int pigsPopIncVillage = 0;
        int pigsPositiveIncVillage = 0;

        int pigsPopPrevAll = 0;
        int pigsPositivePrevAll = 0;

        int overallPigsPopPrevAll = 0;
        int overallPigsPositivePrevAll = 0;

        int pigsPopIncAll = 0;
        int pigsPositiveIncAll = 0;

        int pigletsPopPrevAll = 0;
        int pigletsPositivePrevAll = 0;

        for(int i = 0; i < 5; i++)
        {
            sim.obsSeroIncidencePigsRounds.add(0.0);
            sim.obsSeroPrevalencePigsRounds.add(0.0);
            sim.obsOverallSeroPrevalencePigsRounds.add(0.0);
            sim.obsSeroPrevalencePigletsRounds.add(0.0);

            seroIncidencePigsRoundsAll.add(0.0);
            seroPrevalencePigsRoundsAll.add(0.0);
            overallSeroPrevalencePigsRoundsAll.add(0.0);
            seroPrevalencePigletsRoundsAll.add(0.0);
        }


        for(int i = 1; i < 5; i++)
        {
            pigsPopIncAll = 0;
            pigsPositiveIncAll = 0;

            pigsPopPrevAll = 0;
            pigsPositivePrevAll = 0;

            overallPigsPopPrevAll = 0;
            overallPigsPositivePrevAll = 0;

            pigletsPopPrevAll = 0;
            pigletsPositivePrevAll = 0;

            pigsPopIncVillage = 0;
            pigsPositiveIncVillage = 0;

            pigsPopPrevVillage = 0;
            pigsPositivePrevVillage = 0;

            overallPigsPopPrevVillage = 0;
            overallPigsPositivePrevVillage = 0;

            pigletsPopPrevVillage = 0;
            pigletsPositivePrevVillage = 0;

            for(int cc = 0; cc < sim.censedPigsBag.size(); cc++)
            {
                CensedPig cp = (CensedPig)sim.censedPigsBag.get(cc);

                if(cp.excluded)continue;

                //if(cp.isEligible.get(i) != 1 && cp.isEligible.get(i) != 0)continue;

                //if(cp.roundsVillages.get(i).equals(sim.villageNameNumber))
                if(sim.simW.villagesNamesNumbers.contains(cp.roundsVillages.get(i)))
                {

                    if(cp.roundsCaptured.get(i) != 1)continue;

                    if(cp.isEligible.get(i) == 1)
                    {
                        pigsPopIncVillage++;
                        if(cp.roundsBands.get(i) > 0)pigsPositiveIncVillage++;
                    }

                    //here it consider the seroprevalence of pigs only in the age 
                    //group age > 4 months
                    if(cp.roundsBands.get(i) != -100 
                            && cp.roundsCaptured.get(i) == 1
                            && cp.roundsAgesCorrectedInMonths.get(i) > sim.cohortSeroIncAgesInMonths.get(1)
                      )
                    {
                        pigsPopPrevVillage++;
                        if(cp.roundsBands.get(i) >  0)pigsPositivePrevVillage++;
                    }

                    //System.out.println(sim.cohortSeroIncAges.get(1) + " " + sim.cohortSeroIncAgesInMonths.get(0));

                    if(cp.roundsBands.get(i) != -100)
                    {
                        overallPigsPopPrevVillage++;
                        if(cp.roundsBands.get(i) >  0)overallPigsPositivePrevVillage++;
                    }

                    //here it consider the seroprevalence of pigs only in the age 
                    //group age > 1.5 months and  age < 4 months
                    if(cp.roundsBands.get(i) != -100 
                            && cp.roundsCaptured.get(i) == 1
                            && cp.roundsAgesCorrectedInMonths.get(i) >= sim.cohortSeroIncAgesInMonths.get(0)
                            && cp.roundsAgesCorrectedInMonths.get(i) <= sim.cohortSeroIncAgesInMonths.get(1)
                      )
                    {
                        pigletsPopPrevVillage++;
                        if(cp.roundsBands.get(i) >  0)pigletsPositivePrevVillage++;
                    }

                }


                if(cp.isEligible.get(i) == 1)
                {
                    pigsPopIncAll++;
                    if(cp.roundsBands.get(i) > 0)pigsPositiveIncAll++;
                }

                if(cp.roundsBands.get(i) != -100 
                        && cp.roundsCaptured.get(i) == 1
                        && cp.roundsAgesCorrectedInMonths.get(i) > sim.cohortSeroIncAgesInMonths.get(1)
                  )
                {
                    pigsPopPrevAll++;
                    if(cp.roundsBands.get(i) >  0)pigsPositivePrevAll++;
                }

                if(cp.roundsBands.get(i) != -100)
                {
                    overallPigsPopPrevAll++;
                    if(cp.roundsBands.get(i) >  0)overallPigsPositivePrevAll++;
                }

                if(cp.roundsBands.get(i) != -100 
                        && cp.roundsCaptured.get(i) == 1
                        && cp.roundsAgesCorrectedInMonths.get(i) >= sim.cohortSeroIncAgesInMonths.get(0)
                        && cp.roundsAgesCorrectedInMonths.get(i) <= sim.cohortSeroIncAgesInMonths.get(1)
                  )
                {
                    pigletsPopPrevAll++;
                    if(cp.roundsBands.get(i) >  0)pigletsPositivePrevAll++;
                }
            }

            //village prevs
            sim.obsSeroPrevalencePigletsRounds.set(i, (double)pigletsPositivePrevVillage/(double)pigletsPopPrevVillage);

            sim.obsSeroPrevalencePigsRounds.set(i, (double)pigsPositivePrevVillage/(double)pigsPopPrevVillage);

            sim.obsOverallSeroPrevalencePigsRounds.set(i, (double)overallPigsPositivePrevVillage/(double)overallPigsPopPrevVillage);

            sim.obsSeroIncidencePigsRounds.set(i, (double)pigsPositiveIncVillage/(double)pigsPopIncVillage);


            //entire dataset prevs
            seroPrevalencePigletsRoundsAll.set(i, (double)pigletsPositivePrevAll/(double)pigletsPopPrevAll);

            seroPrevalencePigsRoundsAll.set(i, (double)pigsPositivePrevAll/(double)pigsPopPrevAll);

            overallSeroPrevalencePigsRoundsAll.set(i, (double)overallPigsPositivePrevAll/(double)overallPigsPopPrevAll);

            seroIncidencePigsRoundsAll.set(i, (double)pigsPositiveIncAll/(double)pigsPopIncAll);

            if(sim.extendedOutput)System.out.println("---- village results ---------");
            if(sim.extendedOutput)System.out.println("round: " + i);
            if(sim.extendedOutput)System.out.println("Num. positive piglets prevalence: " + pigletsPositivePrevVillage + " Num.  piglets population: " + pigletsPopPrevVillage);
            if(sim.extendedOutput)System.out.println("Num. positive pigs prevalence: " + pigsPositivePrevVillage + " Num.  pigs population: " + pigsPopPrevVillage);
            if(sim.extendedOutput)System.out.println("Num. positive incidence: " + pigsPositiveIncVillage + " Num. incidence population: " + pigsPopIncVillage);
        }


        if(sim.extendedOutput)System.out.println(" ");
        for(int i = 1; i < 5; i++)
        {
            if(sim.extendedOutput)System.out.println("---- Round " + i + " ----");
            if(sim.extendedOutput)System.out.println(sim.villageName + " Piglets (age < 4 months) SeroPrevalence: " +  sim.obsSeroPrevalencePigletsRounds.get(i));
            if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs (4 > age months) SeroPrevalence: " +  sim.obsSeroPrevalencePigsRounds.get(i));
            if(sim.extendedOutput)System.out.println(sim.villageName + " Overall Pigs SeroPrevalence : " +  sim.obsOverallSeroPrevalencePigsRounds.get(i));
            if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs SeroIncidence: " +  sim.obsSeroIncidencePigsRounds.get(i));

        }


        //System.exit(0);

    }




    //============================================================   
}
