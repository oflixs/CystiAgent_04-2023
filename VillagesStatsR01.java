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


public class VillagesStatsR01 
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

    public int statsVillaProcessed = 0;
    public int statsHouse = 0;
    public int statsLineProcessed = 0;

    public List<Integer> numInTheCohort = new ArrayList<Integer>();//number of time the blood sample 
    public List<Integer> numInTheCohortExpanded = new ArrayList<Integer>();//number of time the blood sample 

    public Boolean allVillages = true;

    //sero incidences and prevalences for the entire R01 trial
    public List<Double> seroIncidencePigsRoundsAll = new ArrayList<Double>();
    public List<Double> seroPrevalencePigletsRoundsAll = new ArrayList<Double>();
    public List<Double> seroPrevalencePigsRoundsAll = new ArrayList<Double>();
    public List<Double> overallSeroPrevalencePigsRoundsAll = new ArrayList<Double>();

    public List<Double> seroIncidenceMTrt = new ArrayList<Double>();
    public List<Double> seroIncidenceMTrtNorm = new ArrayList<Double>();

    public List<Double> seroIncidenceMTrtP = new ArrayList<Double>();
    public List<Double> seroIncidenceMTrtPNorm = new ArrayList<Double>();

    public List<Double> seroIncidenceRingScr = new ArrayList<Double>();
    public List<Double> seroIncidenceRingScrNorm = new ArrayList<Double>();

    public List<Double> seroIncidenceRingScrP = new ArrayList<Double>();
    public List<Double> seroIncidenceRingScrPNorm = new ArrayList<Double>();

    public List<Double> seroIncidenceRingTrt = new ArrayList<Double>();
    public List<Double> seroIncidenceRingTrtNorm = new ArrayList<Double>();

    public List<Double> seroIncidenceRingTrtP = new ArrayList<Double>();
    public List<Double> seroIncidenceRingTrtPNorm = new ArrayList<Double>();

    public List<Double> prevalenceByAge = new ArrayList<Double>();
    public List<Double> prevalenceByAgeNorm = new ArrayList<Double>();

    //to get all villages pigs sero data
    public List<String> villagesList = new ArrayList<String>();
    public HashMap<String, List<Double>> seroPigsVillages = new HashMap<String, List<Double>>();
    public HashMap<String, List<Double>> seroPigletsVillages = new HashMap<String, List<Double>>();
    public HashMap<String, List<Double>> seroIncVillages = new HashMap<String, List<Double>>();

    public HashMap<String, VillageStats> villagesStatsR01Map = new HashMap<String, VillageStats>();

    List<List<Integer>> permutations = new ArrayList<List<Integer>>();

    //====================================================
    public VillagesStatsR01(final SimState pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;
    }

    //====================================================
    //Read the Pigs file ---------------------------
    public void getPigs()
    {

        for(String villName : villagesStatsR01Map.keySet())
        {
            VillageStats village = villagesStatsR01Map.get(villName);

            if(sim.extendedOutput)System.out.println("Pigs processing village: " + villName); 

            readVillagePigs(villName);
        }

    }

    //====================================================
    public void readVillagePigs(String villName)
    {
        if(sim.extendedOutput)System.out.println (" ");
        if(sim.extendedOutput)System.out.println ("----------------------------------------------------");
        if(sim.extendedOutput)System.out.println ("----------------------------------------------------");
        if(sim.extendedOutput)System.out.println ("---- Getting number of pigs from seology R01 ----");
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/populationsData/R01/R01OA_Cerdos_11.10.17_includes all pigs base and final.xlsx";
        sheetName = "data_Final";
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

                if(statsRows > 0)processPigLineR01(line, villName);
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


    }

    //====================================================
    public void processPigLineR01(List<String> line, String villName)
    {
        statsLineProcessed++;

        Boolean localPrintOut = false;
        int statsSero = 0;

        int statsPigs = 0;
        int statsPigsTot = 0;

        int indexT = 6;

        int roundLenght = 22;

        int location = 0;

        int numBands = 0;

        int age = 0;
        int ageInMonths = 0;

        int censed = 0;

        String wbValue = "";

        //System.out.println (housesLinesMap.get(91));


        for(int round = 1; round < 9; round++)
        {

            if(round != 1)location = 12 + (round - 1) * roundLenght;
            else location = 11;

            String vill = line.get(location);

            if(vill.equals("."))continue;

            if(vill.equals("510")
                    || vill.equals("517")   
                    || vill.equals("568")   
                    || vill.equals("569")   
                    || vill.equals("592")   
                    || vill.equals("593")   
                    || vill.equals("594")   
              )continue;

            if(round == 2)if(localPrintOut)System.out.println ("--------------");
            if(round == 2)if(localPrintOut)System.out.println ("round: " + round);
            if(round == 2)if(localPrintOut)System.out.println (location);
            if(round == 2)if(localPrintOut)System.out.println ("Village: " + line.get(location));

            //collect baseline data ----------------------
            if(round == 1 && vill.equals(villName))
            {
                if(localPrintOut)System.out.println ("round: " + round);
                //if(round != 1)location = 12 + (round - 1) * roundLenght + 1;
                //else location = 12;

                if(line.get(location).equals("."))continue;
                int hhShpId = Integer.parseInt(line.get(location));

                Household hh = sim.householdsGen.getHouseholdByshpId(hhShpId);

                if(hh == null)
                {
                    if(sim.extendedOutput)System.out.println ("Household with shpId: " + hhShpId + "no found");
                    //System.exit(0);
                }

                if(localPrintOut)System.out.println ("round: " + round);


                //Pig pig = new Pig(state, hh, false, false);
                VillageStats village = villagesStatsR01Map.get(villName);
                village.addPig();

                village.R01InterventionArm = Integer.parseInt(line.get(3));
                if(localPrintOut)System.out.println (sim.village.R01InterventionArm);
                //System.exit(0);
            }

            //if(round == 2)System.out.println("pig: " + ID + " round: " + round + " num bands: " + numBands); 

        }


        //System.exit(0);



    }


    //====================================================
    public void getHousesAndHumans()
    {
        String inputFile = "";
        String sheetName = "";
        inputFile = "./inputData/populationsData/R01/R01 Rings_Baseline census 23 villas_03232020.xls";
        sheetName = "R01 Baseline Census";

        try{
            Workbook workbookFile = WorkbookFactory.create(new FileInputStream(inputFile));
            //XSSFWorkbook workbookFile = new XSSFWorkbook(new FileInputStream(ext.filePriorABC));

            Sheet sheet = workbookFile.getSheet(sheetName);

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

                //if(sim.extendedOutput)System.out.println ("line 2 and 3: " + line.get(2) + ", "  + line.get(3));
                
                if(villagesStatsR01Map.containsKey(line.get(1)))
                {
                    VillageStats village = villagesStatsR01Map.get(line.get(1));
                    village.addHuman();

                    List<String> housesNumbers = village.housesNumbers;
                    if(!housesNumbers.contains(line.get(2)))housesNumbers.add(line.get(2));
                    village.housesNumbers = housesNumbers;
                }
                else
                {
                    VillageStats village = new VillageStats(sim);
                     
                    village.name = line.get(1);

                    village.addHuman();

                    List<String> housesNumbers = village.housesNumbers;
                    if(!housesNumbers.contains(line.get(2)))housesNumbers.add(line.get(2));
                    village.housesNumbers = housesNumbers;

                    villagesStatsR01Map.put(line.get(1), village);
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


        //System.out.println("houseLines size: " + houseLines.size());
        //System.out.println(houseLines);
        //System.out.println("housesLinesMap size: " + housesLinesMap.size());
        //System.out.println(housesLinesMap.get(2));
        //System.exit(0);
    }


    //====================================================
    public void printResume()
    {

        if(sim.extendedOutput)System.out.println (" ");
        if(sim.extendedOutput)System.out.println ("--------------------------------------");
        if(sim.extendedOutput)System.out.println ("---- Resume of villages stats");
        if(sim.extendedOutput)System.out.println ("tot num of villages: " + villagesStatsR01Map.size());

        for(String villName : villagesStatsR01Map.keySet())
        {
            VillageStats village = villagesStatsR01Map.get(villName);
            if(sim.extendedOutput)System.out.println ("Village: " + villName 
                    + " Intervention arm: " + village.R01InterventionArm 
                    + " num of Houses: " + village.housesNumbers.size() 
                    + " num of Humans: " + village.nHumans
                    + " num of Pigs: " + village.nPigs
                    );
        }

    }

    //====================================================
    public void separateVillagesForCalibration()
    {
        //create the 6 trial arms sub-groups
        List<Integer> interventions = new ArrayList<Integer>();

        for(String villName : villagesStatsR01Map.keySet())
        {
            VillageStats village = villagesStatsR01Map.get(villName);

            if(village.name.equals("571"))
            {
                if(sim.extendedOutput)System.out.println ("---- Village 571 excluded");
                continue;
            }
        
            if(!interventions.contains(village.R01InterventionArm))interventions.add(village.R01InterventionArm);
        }

        List<VillageStats> interVilla = new ArrayList<VillageStats>();

        List<Integer> subGroupsIndexs1 = new ArrayList<Integer>();
        List<Double> subGroupsScores1  = new ArrayList<Double>();
        List<List<Integer>> subGroupsVillages1 = new ArrayList<List<Integer>>();

        List<Integer> subGroupsIndexs2 = new ArrayList<Integer>();
        List<Double> subGroupsScores2  = new ArrayList<Double>();
        List<List<Integer>> subGroupsVillages2 = new ArrayList<List<Integer>>();

        for(int i = 0; i < interventions.size(); i++)
        {
            int interv = interventions.get(i);
            interVilla = new ArrayList<VillageStats>();
            List<Integer> indexs = new ArrayList<Integer>();
            HashMap<Integer, Double> scores = new HashMap <Integer, Double>();
            permutations = new ArrayList<List<Integer>>();

            if(sim.extendedOutput)System.out.println(" ");
            if(sim.extendedOutput)System.out.println("----------");
            if(sim.extendedOutput)System.out.println("Interventions group: " + interv);

            for(String villName : villagesStatsR01Map.keySet())
            {
                VillageStats village = villagesStatsR01Map.get(villName);

                if(village.name.equals("571"))
                {
                    if(sim.extendedOutput)System.out.println ("---- Village 571 excluded");
                    continue;
                }

                if(village.R01InterventionArm != interv)continue;

                interVilla.add(village);
                
                int ind = Integer.parseInt(village.name);
                indexs.add(ind);

                double score = village.nHumans + village.nPigs;
                scores.put(ind, score);

                if(sim.extendedOutput)System.out.println("Village: " + village.name + " score: " + score);
            }
            
            permutations = new ArrayList<List<Integer>>();
            permute(indexs, 0);

            if(sim.extendedOutput)System.out.println(" ");
            if(sim.extendedOutput)System.out.println("Permutations of villages indexs: ");
                //if(sim.extendedOutput)System.out.println(permutations);

            double intMinScore = 1000000000;
            int minIndex = 0;

            for(int j = 0; j < permutations.size(); j++)
            {
                List perm = permutations.get(j);


                double diff = 0;
                double score1 = 0;
                double score2 = 0;

                if(perm.size() == 4)
                {
                    score1 = score1 + scores.get(perm.get(0));
                    score1 = score1 + scores.get(perm.get(1));

                    score2 = score2 + scores.get(perm.get(2));
                    score2 = score2 + scores.get(perm.get(3));
                }
                else if(perm.size() == 3)
                {
                    score1 = score1 + scores.get(perm.get(0));
                    score1 = score1 + scores.get(perm.get(1));

                    score2 = score2 + scores.get(perm.get(2));
                }

                diff = Math.abs(score1 - score2);

                if(sim.extendedOutput)System.out.println("Permutation: " + perm + " Score1: " + score1 + " score2: " + score2 + " diff: " + diff);

                if(diff < intMinScore)
                {
                    intMinScore = diff;
                    minIndex = j;
                }
            }

            if(sim.extendedOutput)System.out.println(" ");
            if(sim.extendedOutput)System.out.println("-----------------------------------");
            if(sim.extendedOutput)System.out.println("Minimum score permutation: ");
            if(sim.extendedOutput)System.out.println(permutations.get(minIndex));
            if(sim.extendedOutput)System.out.println("Min diff: " + intMinScore);

            List perm = permutations.get(minIndex);

            List<Integer> tmp1 = new ArrayList<Integer>();
            List<Integer> tmp2 = new ArrayList<Integer>();

            double score1 = 0;
            double score2 = 0;

            if(perm.size() == 4)
            {
                tmp1.add((Integer)perm.get(0));
                tmp1.add((Integer)perm.get(1));

                tmp2.add((Integer)perm.get(2));
                tmp2.add((Integer)perm.get(3));

                score1 = score1 + scores.get(perm.get(0));
                score1 = score1 + scores.get(perm.get(1));

                score2 = score2 + scores.get(perm.get(2));
                score2 = score2 + scores.get(perm.get(3));
            }
            if(perm.size() == 3)
            {
                tmp1.add((Integer)perm.get(0));
                tmp1.add((Integer)perm.get(1));

                tmp2.add((Integer)perm.get(2));

                score1 = score1 + scores.get(perm.get(0));
                score1 = score1 + scores.get(perm.get(1));

                score2 = score2 + scores.get(perm.get(2));
            }

            subGroupsVillages1.add(tmp1);
            subGroupsVillages2.add(tmp2);

            subGroupsScores1.add(score1);
            subGroupsScores2.add(score2);
        }//loop on intervention arm

        //create the calibration and validation groups

        int length = subGroupsVillages1.size();
        long max = 1 << length;

        int stats = -1;

        permutations = new ArrayList<List<Integer>>();

        double minDist = 100000000;
        int    minDistIndex = 0;

        for (long b = 0; b < max; b++) 
        {
            long currentNumber = b;
            final char[] buffer = new char[length];
            int bufferPosition = buffer.length;
            while (bufferPosition > 0) {
                buffer[--bufferPosition] = (char) (48 + (currentNumber & 1));
                currentNumber >>>= 1;
            }
            //System.out.println(buffer);

            stats++;

            List<Integer> perm = new ArrayList<Integer>();
            double score1 = 0;
            double score2 = 0;

            for(int i = 0; i < buffer.length; i++)
            {
                char character = buffer[i];
                if(character == '0')
                {
                    perm.add(0);
                    score1 = score1 + Math.abs(subGroupsScores1.get(i));
                    score2 = score2 + Math.abs(subGroupsScores2.get(i));

                }
                if(character == '1')
                {
                    perm.add(1);
                    score1 = score1 + Math.abs(subGroupsScores2.get(i));
                    score2 = score2 + Math.abs(subGroupsScores1.get(i));

                }

            }


            double dist = Math.abs(score1 - score2);

            if(sim.extendedOutput)System.out.println("Permutation: " + perm + " Score1: " + score1 + " score2: " + score2 + " dist: " + dist);

            permutations.add(perm);

            if(dist < minDist)
            {
                minDist = dist;
                minDistIndex = stats;
            }
        }

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println("-----------------------------------");
        if(sim.extendedOutput)System.out.println("Minimum score permutation: ");
        if(sim.extendedOutput)System.out.println(permutations.get(minDistIndex));
        if(sim.extendedOutput)System.out.println("Min diff: " + minDist);

        List perm = permutations.get(minDistIndex);

        List<VillageStats> villagesCalibration = new ArrayList<VillageStats>();
        List<VillageStats> villagesValidation  = new ArrayList<VillageStats>();

        for(int i = 0; i < perm.size(); i++)
        {
            List<Integer> tmp1 = new ArrayList<Integer>();
            List<Integer> tmp2 = new ArrayList<Integer>();

            if(sim.extendedOutput)System.out.println("----");
            if(sim.extendedOutput)System.out.println(i + " " + perm.get(i));

            if((Integer)perm.get(i) == 0)
            {
                tmp1 = subGroupsVillages1.get(i);
                tmp2 = subGroupsVillages2.get(i);
            }
            else if((Integer)perm.get(i) == 1)
            {
                tmp1 = subGroupsVillages2.get(i);
                tmp2 = subGroupsVillages1.get(i);
            }

            if(sim.extendedOutput)System.out.println("tmp1: " + tmp1);
            if(sim.extendedOutput)System.out.println("tmp2: " + tmp2);

            for(int j = 0; j < tmp1.size(); j++)
            {
                int villId = tmp1.get(j);
                VillageStats vill1 = getVillageFromId(villId);
                villagesCalibration.add(vill1);
            }

            for(int j = 0; j < tmp2.size(); j++)
            {
                int villId = tmp2.get(j);
                VillageStats vill2 = getVillageFromId(villId);
                villagesValidation.add(vill2);
            }


        }


        //print Results -----------------


        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println("-----------------------------------");
        if(sim.extendedOutput)System.out.println("-----------------------------------");
        if(sim.extendedOutput)System.out.println("Calibration Villages:");
        if(sim.extendedOutput)System.out.println("Num villages: " + villagesCalibration.size());

        int pop = 0;
        for(int i = 0; i < villagesCalibration.size(); i++)
        {
            VillageStats vill = villagesCalibration.get(i);
            pop = pop + vill.nHumans + vill.nPigs;
            if(sim.extendedOutput)System.out.println(vill.name);
            //vill.printResume();
        }
        if(sim.extendedOutput)System.out.println("Tot humans and pigs population: " + pop);

        for(int i = 0; i < villagesCalibration.size(); i++)
        {
            VillageStats vill = villagesCalibration.get(i);
            vill.printResume();
        }


        //----------------------------------------------------

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println("-----------------------------------");
        if(sim.extendedOutput)System.out.println("-----------------------------------");
        if(sim.extendedOutput)System.out.println("Validation Villages:");
        if(sim.extendedOutput)System.out.println("Num villages: " + villagesValidation.size());

        pop = 0;
        for(int i = 0; i < villagesValidation.size(); i++)
        {
            VillageStats vill = villagesValidation.get(i);
            pop = pop + vill.nHumans + vill.nPigs;
            if(sim.extendedOutput)System.out.println(vill.name);
            //vill.printResume();
        }
        if(sim.extendedOutput)System.out.println("Tot humans and pigs population: " + pop);



        for(int i = 0; i < villagesValidation.size(); i++)
        {
            VillageStats vill = villagesValidation.get(i);
            vill.printResume();
        }

    }

    //====================================================
    //get all the permutation of an array
    public void permute(List<Integer> arr, int k)
    {
        for(int i = k; i < arr.size(); i++)
        {
            java.util.Collections.swap(arr, i, k);
            permute(arr, k+1);
            java.util.Collections.swap(arr, k, i);
        }

        if(k == arr.size() -1)
        {
            List tmp = new ArrayList<Integer>(arr);
            permutations.add(tmp);
            //return arr;
            //System.out.println(java.util.Arrays.toString(arr.toArray()));
        }
    }

    //====================================================
    //iterates over the lenght first binaries
    void printAllBinaryUpToLength(final int length) 
    {
        if (length >= 63)
            throw new IllegalArgumentException("Current implementation supports only a length < 63. Given: " + length);

        long max = 1 << length;

        for (long i = 0; i < max; i++) 
        {
            long currentNumber = i;
            final char[] buffer = new char[length];
            int bufferPosition = buffer.length;
            while (bufferPosition > 0) {
                buffer[--bufferPosition] = (char) (48 + (currentNumber & 1));
                currentNumber >>>= 1;
            }
            System.out.println(buffer);
        }
    }

    //====================================================
    public VillageStats getVillageFromId(int id)
    {
        for(String villName : villagesStatsR01Map.keySet())
        {
            VillageStats village = villagesStatsR01Map.get(villName);
            if(id == Integer.parseInt(village.name))return village;
        }
        return null;

    }



    //============================================================   
}
