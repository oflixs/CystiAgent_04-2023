package sim.app.cystiagents;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import java.io.FileWriter;
import java.io.*;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import java.util.Properties;

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

import sim.util.geo.MasonGeometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import com.vividsolutions.jts.geom.GeometryFactory;

public class StudyVillagesStats {

    private static final long serialVersionUID = 1;

    public CystiAgents simVilla;

    //Directories, files and IO -------------------------
    public static String simName = "";
    public static String worldInputFile = "";
    public static String rootDir = "";

    //Simulation parameters -----------------------------
    //in the sdr output
    public long ctSleep = 3000;

    //World parameters ----------------------------------
    List<String> villagesNames = new ArrayList<String>(); 
    List<String> villagesNamesNumbers = new ArrayList<String>(); 
    List<String> villagesGroup = new ArrayList<String>(); 
    static int numVilla = 0;
    List<Village> villages = new ArrayList<Village>(); 
    public int numRunning = 0;
    public int numVillaSteps = 0;
    public int readyCommunities = 0;
    public int stoppedCommunities = 0;
    public int numStepSync = 0;
    public int villageId = 0;
    public int numPrint = 0;

    //variable to be used with ABC calibration method
    public static String ABCTime = "";
    public static Boolean ABC = false;

    //Sim Classes ---------------------------------------
    public ControlThread ctrlThread = null;
    public ReadInput input;

    public List<CensedHousehold> censedHouses = new ArrayList<CensedHousehold>();

    CystiAgentsWorld simW = null;

    public List<Double> decPerc = new ArrayList<Double>();
    public List<Double> decPercNorm = new ArrayList<Double>();
    public List<Double> fracts = new ArrayList<Double>();

    public Point villageCenter = null;//geographic center of the village

    //====================================================
    public StudyVillagesStats(CystiAgentsWorld psimW)
    {
        simW = psimW;
    }

    //====================================================
    public void readInput()
    {
        worldInputFile = "paramsFiles/" + worldInputFile; 

        //if(ABC)System.out.println (worldInputFile);
        input = new ReadInput(worldInputFile, rootDir, false);

        villagesNames = input.readListString("villagesNames");
        numVilla = villagesNames.size();

        Set<String> set = new HashSet<String>(villagesNames);
        if(set.size() < numVilla)
        {
            System.out.println ("Duplicate village name found in worldInputFile");
            System.out.println ("Program Stops");
            System.exit(0);
        
        }

        if(numVilla == 0)
        {
            System.out.println ("Number of villages in worldInputFile = 0");
            System.out.println ("Program Stops");
            System.exit(0);
        }
        else
        {
            if(!ABC)System.out.println ("Number of villages in worldInputfile = " + numVilla);
        
        }

        numStepSync = input.readInt("numStepSync");
       if(!ABC)System.out.println ("numStepSync = " + numStepSync);

        if(numStepSync == 0)
        {
            System.out.println ("Number of sync steps = 0");
            System.out.println ("Program Stops");
            System.exit(0);
        }


    }

    //====================================================
    public void getVillagesNamesNumbers()
    {
        villagesNamesNumbers = new ArrayList<String>(); 

        for(int i = 0; i < villagesNames.size(); i++)
        {
            String name = (String)villagesNames.get(i);

            String delims = "_";
            String[] words = name.split(delims);

            Boolean loop = true;
            while(loop)
            {
                if(words[1].charAt(0) == '0')
                {
                    String tmp = words[1].substring(1, words[1].length());
                    words[1] = tmp;
                }
                else loop = false;
            }
            villagesNamesNumbers.add(words[1]);
            villagesGroup.add(words[0]);
            //System.out.println ("Village");
        }
        //System.exit(0);
    }

    //====================================================
    public void studyVillagesDensity()
    {
        System.out.println(" ");
        System.out.println("------------------------------------------- ");
        System.out.println("--- Study the villages stats ");

        if(simW.simName.equals("TTEMP"))
        {
            worldInputFile = "TTEMP_coreInput.params";
        }
        if(simW.simName.equals("R01"))
        {
            worldInputFile = "R01_coreInput.params";
        }
        //System.exit(0);

        readInput();

        //Starts the villages simulations --------
        CyclicBarrier barrier = null;

        barrier = new CyclicBarrier(numVilla);

        for (int j = 0; j < numVilla; j++)
        {
            String name = villagesNames.get(j);

            if(!ABC)System.out.println ("Village name from world: ----" + name + "------");

            Village village = new Village(System.currentTimeMillis(), j, simW, barrier);

            villages.add(village);

            //schedule.scheduleRepeating(village);

            village.init();

            CystiAgents simVilla = village.getsimVilla();
            simVilla.start();

            System.out.println ("Getting village stats");

            //getStatsVillage(village);

            numRunning++;

            try {         
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Double> ranges = new ArrayList<Double>(); 

        ranges.add(10.0);
        ranges.add(50.0);
        ranges.add(100.0);
        ranges.add(200.0);
        ranges.add(300.0);
        ranges.add(400.0);
        ranges.add(500.0);
        ranges.add(600.0);
        ranges.add(700.0);
        ranges.add(800.0);
        ranges.add(900.0);
        ranges.add(1000.0);
        ranges.add(1100.0);
        ranges.add(1200.0);
        ranges.add(1300.0);
        ranges.add(1400.0);
        ranges.add(1500.0);
        ranges.add(1500.0);
        ranges.add(1600.0);
        ranges.add(1700.0);
        ranges.add(1800.0);
        ranges.add(1900.0);
        ranges.add(2000.0);

        for(int i = 0; i < ranges.size(); i++)
        {
            simW.rangeLimit = ranges.get(i);

            getStatsVillagesGlobal();

            System.out.println ("Getting village dist from village center deciles"); 

            Collections.sort(censedHouses, new CensedHouseholdsDistComparator());
            //getVillageCenterDistDec();

            Collections.sort(censedHouses, new CensedHouseholdsSeroComparator());

            studyVillagesDensityWriteToFile(simW.simName);
        }


    }

    //====================================================
    public void getStatsVillage(Village village)
    {
            //sim.householdsGen.generateHHVoronoi();
            simVilla = village.getsimVilla();

            getHHDistances();
            simVilla.householdsGen.getGenderRatio();
            getHHsValuesInRange();
            getPigsPrevalenceInRange();
            //System.exit(0);

            simVilla.householdsGen.calculateHouseholdsCenterPoint();
            //System.exit(0);


            //for(int i = 0; i < censedHouses.size(); i++)
            //{
            //    CensedHousehold chh = (CensedHousehold)censedHouses.get(i);
            //    chh.printResume();
            //}

            int lmin = 0;
            int lmax = 0;

            if(simVilla.simW.simName.equals("TTEMP"))
            {
                lmin = 4;
                lmax = 5;
            }
            if(simVilla.simW.simName.equals("R01"))
            {
                lmin = 7;
                lmax = 8;
            }


            if(simName.equals("R01"))
            {
                lmin = 1;
                lmax = 2;
            }


            int numRounds = lmax - lmin;

            //simVilla.pigsGenTTEMP.calculateIncidencesTTEMP();
            //System.exit(0);

            //System.out.println(simVilla.householdsBag.size());

            for(int j = 0; j < simVilla.householdsBag.size(); j++)
            {
                Household hh = (Household)simVilla.householdsBag.get(j);

                String name = Integer.toString(hh.shpId);
                CensedHousehold chh = new CensedHousehold(name);

                String simName = simW.simName;
                chh.village = simName + "_" + simVilla.villageNameNumber;

                chh.shpId = hh.shpId;

                chh.point = hh.geoPoint;

                chh.numHumans = hh.humans.size();
                chh.numInfectedHumans = hh.numInfHumans;

                chh.genderRatio = hh.genderRatio;
                chh.genderRatioInfected = hh.genderRatioInfected;

                for(int round = lmin; round < lmax; round++)
                {
                    chh.numPigs = chh.numPigs + hh.numPigsRound.get(round);
                    chh.seropositivePigs = chh.seropositivePigs + hh.seropositivePigs.get(round);

                    chh.pigsInRange = chh.pigsInRange + hh.pigsInRange.get(round);
                    chh.pigsInRangePositive = chh.pigsInRangePositive + hh.pigsInRangePositive.get(round);

                    //if(chh.name.equals("TTEMP569_12") && round == 4)
                    //{
                    //    System.out.println("-----------------------------");
                    //    System.out.println(chh.pigsInRange);
                    //    System.out.println(chh.pigsInRangePositive);
                    //}

                }

                chh.pigsInRangeRound = hh.pigsInRange;
                chh.pigsInRangePositiveRound = hh.pigsInRangePositive;

                chh.numInfectedPigs = hh.numInfPigs;

                chh.sshhValue = hh.sshhValue;
                chh.sshhValueInRange = hh.sshhValueInRange;
                chh.eliminahecValue = hh.eliminahecValue;
                chh.eliminahecValueInRange = hh.eliminahecValueInRange;

                chh.numPigsInRange = hh.numPigsInRange;
                chh.numHumansInRange = hh.numHumansInRange;

                chh.numInfectedPigsInRange = hh.numInfPigsInRange;
                chh.numInfectedHumansInRange = hh.numInfHumansInRange;

                chh.genderRatioInRange = hh.genderRatioInRange;
                chh.genderRatioInfectedInRange = hh.genderRatioInfectedInRange;


                chh.avgDistFromVillage = hh.avgDistFromVillage;
                chh.distFromVillageCenter = hh.distFromVillageCenter;
                chh.centerDistDecile = hh.CenterDistDecile;
                chh.hhInRange = hh.numHHInRange;

                if(chh.numPigs !=0)chh.avgPigsSeroprevalence = (double)chh.seropositivePigs/(double)(chh.numPigs * (double)numRounds);
                else chh.avgPigsSeroprevalence = -100.0;
                if(chh.pigsInRange !=0)chh.pigsSeroprevalenceInRange = (double)chh.pigsInRangePositive/(double)(chh.pigsInRange * (double)numRounds);
                else chh.pigsSeroprevalenceInRange = -100.0;

                //chh.printResume();

                censedHouses.add(chh);
            }


    }



    //====================================================
    public void studyVillagesDensityWriteToFile(String what)
    {
        HSSFWorkbook workbook = new HSSFWorkbook();
        String sName   = "villages data";
        HSSFSheet sheet = workbook.createSheet(sName);

        Cell cell = null;

        int lastRow = sheet.getLastRowNum();

        Row row = null;

        int cellnum = 0;

        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Village");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Household");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"NumHumans");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"NumHumansInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"NuminfectedHumans");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"NuminfectedHumansInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"genderRatio");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"genderRatioInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"genderRatioInfected");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"genderRatioInfectedInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"NumPigs");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"NumPigsInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"numInfectedPigs");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"numInfectedPigsInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"sshhValue");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"sshhValueInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"eliminahecValue");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"eliminahecValueInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"avgDistFromTheVillage");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"distFromTheVillageCenter");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"avgPigsSeroprevalence");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"hhInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"pigsSeroprevalenceInRange");


        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"nElim1");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"nSshh4");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"corrCondInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"corrMatInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"outDefecatorsInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"noBathroomInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"scaledEliminahecInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"scaledSshhValuesInrange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"scaledCorrCondInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"scaledCorrMatInRange");



        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"No100pigsSeroprevalenceInRange");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"centerDistDecile");

        for(int i = 0; i < censedHouses.size(); i++)
        {
            CensedHousehold chh = (CensedHousehold)censedHouses.get(i);

            row = sheet.createRow(lastRow);
            lastRow++;
            cellnum = 0;

            cell = row.createCell(cellnum);
            cell.setCellValue((String)(chh.village));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((String)(chh.name));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numHumans));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numHumansInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numInfectedHumans));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numInfectedHumansInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.genderRatio));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.genderRatioInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.genderRatioInfected));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.genderRatioInfectedInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numPigs));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numPigsInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numInfectedPigs));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.numInfectedPigsInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.sshhValue));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.sshhValueInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.eliminahecValue));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.eliminahecValueInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.avgDistFromVillage));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.distFromVillageCenter));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.avgPigsSeroprevalence));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.hhInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.pigsSeroprevalenceInRange));
            cellnum++;



            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.nElim1InRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.nSshh4InRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.corrCondInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.corrMatInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.outDefecatorsInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.noBathroomInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.scaledEliminahecValueInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.scaledSshhValueInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.scaledCorrCondInRange));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.scaledCorrMatInRange));
            cellnum++;


            double tmp = chh.pigsSeroprevalenceInRange;
            if(tmp == -100)tmp = 0.0;
            cell = row.createCell(cellnum);
            cell.setCellValue((double)(tmp));
            cellnum++;

            cell = row.createCell(cellnum);
            cell.setCellValue((double)(chh.centerDistDecile));
            cellnum++;


        }

        //write to file ----
        int rl = (int)Math.round(simW.rangeLimit);
        String fileName = "outputs/" + what  + "_villagesDensityData_" + rl  + ".xls";
        try {

            FileOutputStream out = 
                new FileOutputStream(new File(fileName));
            workbook.write(out);
            out.close();
            System.out.println("Output village density data spreadsheet written sucessfully.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    //}

}
    //====================================================
    public void getVillageCenterDistDec()
    {
        System.out.println ("");
        System.out.println ("----------------------------");
        System.out.println ("Getting the village center dist deciles");
        for(int i = 0; i < simW.numDeciles; i ++)
        {
            decPerc.add(0.0);
            decPercNorm.add(0.0);
        }

        double avgSeroInRangeInfected = 0.0;
        double avgSeroInRangeInfectedNorm = 0.0;

        double avgSeroInRangeNotInfected = 0.0;
        double avgSeroInRangeNotInfectedNorm = 0.0;

        double avgSeroInRangeAll = 0.0;
        double avgSeroInRangeAllNorm = 0.0;

        for(int i = 0; i < censedHouses.size(); i++)
        {
            CensedHousehold chh = (CensedHousehold)censedHouses.get(i);

            if(chh.pigsInRange <= 2)continue;

            if(chh.pigsSeroprevalenceInRange >= 0)
            {
                avgSeroInRangeAll = avgSeroInRangeAll + chh.pigsSeroprevalenceInRange;
                avgSeroInRangeAllNorm++;

                if(chh.numInfectedHumans == 0)
                {
                    avgSeroInRangeNotInfected = avgSeroInRangeNotInfected + chh.pigsSeroprevalenceInRange;
                    avgSeroInRangeNotInfectedNorm++;
                }
            }

            if(chh.numInfectedHumans == 0)continue;

            System.out.println ("----------------------------");

            Household hh = simVilla.householdsGen.getHouseholdByshpId(chh.shpId);

            System.out.println (chh.name + " passed inf humans " + chh.numInfectedHumans);
            System.out.println ("Household: " + chh.name + " num infected humans: "+ chh.numInfectedHumans);
            System.out.println ("sshh value: " + chh.sshhValue + " eliminahecValue: " + chh.eliminahecValue);
            System.out.println ("pigs in range: ");
            System.out.println (chh.pigsInRangeRound);
            System.out.println ("pigs in range seropositive: ");
            System.out.println (chh.pigsInRangePositiveRound);
            System.out.println ("pigs in range seropositive round 7: " + hh.pigsInRangePositive.get(7));

            //hh.printResume();

            if(chh.pigsSeroprevalenceInRange < 0)continue;

            if(!chh.name.equals("TTEMP510_31"))
            {
                avgSeroInRangeInfected = avgSeroInRangeInfected + chh.pigsSeroprevalenceInRange;
                avgSeroInRangeInfectedNorm++;
                System.out.println ("avgSero: " + avgSeroInRangeInfected);
                System.out.println ("avgSeroNorm: " + avgSeroInRangeInfectedNorm);
                System.out.println ("Avg seroprevalence in range household with tapeworm carrier: " + avgSeroInRangeInfected/avgSeroInRangeInfectedNorm);
            }
 

            System.out.println (chh.name + " passed pigs seroPreve in range " + chh.pigsSeroprevalenceInRange);

            decPerc.set(chh.centerDistDecile, (decPerc.get(chh.centerDistDecile) + chh.pigsSeroprevalenceInRange));
            decPercNorm.set(chh.centerDistDecile, (decPercNorm.get(chh.centerDistDecile) + 1.0));

            System.out.println ("Household: " + chh.name + " distance from village: " + chh.distFromVillageCenter + " num infected humans: "+ chh.numInfectedHumans  + " pigs seropositivity in range: " + chh.pigsSeroprevalenceInRange);
        }

        System.out.println (decPerc);
        System.out.println (decPercNorm);

        System.out.println ("-----------------------------------------------------------");
        System.out.println ("-----------------------------------------------------------");
        System.out.println (" ");
        System.out.println ("-----------------------------------------------------------");
        System.out.println ("Pigs seropositivity in range: " + simW.rangeLimit + " divided in deciles based on the household distances from village center");
        for(int i = 0; i < simW.numDeciles; i++)
        {
            System.out.println ("Houses in range decile " + i + " avg pig seroprevalence: " + decPerc.get(i)/decPercNorm.get(i));

        }

        System.out.println ("Avg seroprevalence in range all household: " + avgSeroInRangeAll/avgSeroInRangeAllNorm);
        System.out.println ("Avg seroprevalence in range household WITH tapeworm carrier: " + avgSeroInRangeInfected/avgSeroInRangeInfectedNorm);
        System.out.println ("Avg seroprevalence in range household WITHOUT tapeworm carrier: " + avgSeroInRangeNotInfected/avgSeroInRangeNotInfectedNorm);

        System.out.println ("-----------------------------------------------------------");
        System.out.println ("-----------------------------------------------------------");


    }


    //====================================================
    public void getHHDistances()
    {
        double dist = 0.0;

        for(int i = 0; i < simVilla.householdsBag.size(); i ++)
        {
            Household hh1 = (Household)simVilla.householdsBag.get(i);

            Point pHome1 = (Point)hh1.mGeometry.getGeometry();

            dist = 0.0;

            for(int j = 0; j < simVilla.householdsBag.size(); j ++)
            {
                Household hh2 = (Household)simVilla.householdsBag.get(j);

                Point pHome2 = (Point)hh2.mGeometry.getGeometry();

                dist = dist + pHome1.distance(pHome2);
            }

            hh1.avgDistFromVillage = dist/(double)simVilla.householdsBag.size();
            //System.out.println ("Household: " + hh1.shpId + " distance from village: " + hh1.avgDistFromVillage);

        }
    }

    //====================================================
    public void getHHDistancesGlobal()
    {
        double dist = 0.0;

        for(int i = 0; i < simW.households.size(); i ++)
        {
            Household hh1 = (Household)simW.households.get(i);

            Point pHome1 = (Point)hh1.mGeometry.getGeometry();

            dist = 0.0;

            for(int j = 0; j < simW.households.size(); j ++)
            {
                Household hh2 = (Household)simW.households.get(i);

                Point pHome2 = (Point)hh2.mGeometry.getGeometry();

                dist = dist + pHome1.distance(pHome2);
            }

            hh1.avgDistFromVillage = dist/(double)simW.households.size();
            //System.out.println ("Household: " + hh1.shpId + " distance from village: " + hh1.avgDistFromVillage);

        }
    }



    //====================================================
    public void getHHsValuesInRange()
    {
        double elim = 0;
        double ssh = 0;
        double pigsr = 0;
        double humansr = 0;
        double pigsInfr = 0;
        double humansInfr = 0;
        
        double gender = 0;
        double genderInf = 0;

        double avgNumInRange = 0.0;

        for(int i = 0; i < simVilla.householdsBag.size(); i ++)
        {
            Household hh1 = (Household)simVilla.householdsBag.get(i);

            Point pHome1 = (Point)hh1.mGeometry.getGeometry();

            elim = 0;
            ssh = 0;
            pigsr = 0;
            humansr = 0;
            pigsInfr = 0;
            humansInfr = 0;

            gender = 0;
            genderInf = 0;

            double refArea = Math.PI * Math.PI * simVilla.simW.rangeLimit * simVilla.simW.rangeLimit;

            int numHe = 0;
            int numHs = 0;

            for(int j = 0; j < simVilla.householdsBag.size(); j ++)
            {
                Household hh2 = (Household)simVilla.householdsBag.get(j);

                Point pHome2 = (Point)hh2.mGeometry.getGeometry();

                if(pHome1.distance(pHome2) <= simVilla.simW.rangeLimit)
                {
                    hh1.numHHInRange++;
                    if(hh2.eliminahecValue > 0)
                    {
                        elim = elim + hh2.eliminahecValue;
                        numHe++;
                    }
                    if(hh2.sshhValue > 0)
                    {
                        ssh = ssh + hh2.sshhValue;
                        numHs++;
                    }

                    //if(simVilla.extendedOutput)System.out.println (hh2.shpId + " household h2 eliminahecValue: " + hh2.eliminahecValue);

                    if(hh2.humans.size() >= 0)humansr = humansr + hh2.humans.size();
                    if(hh2.humans.size() >= 0)humansInfr = humansInfr + hh2.numInfHumans;

                    if(hh2.pigs.size() >= 0)pigsInfr = pigsInfr + hh2.numInfPigs;
                    if(hh2.pigs.size() >= 0)pigsr = pigsr + hh2.pigs.size();

                    if(hh2.genderRatio >= 0)gender = gender + hh2.genderRatio;
                    if(hh2.genderRatioInfected >= 0)genderInf = genderInf + hh2.genderRatioInfected;
                }
            }

            hh1.eliminahecValueInRange = elim/(double)numHe;
            hh1.sshhValueInRange = ssh/numHs;

            hh1.numHumansInRange = humansr/refArea;;
            hh1.numInfHumansInRange = humansInfr/refArea;;

            hh1.numPigsInRange = pigsr/refArea;;
            hh1.numInfPigsInRange = pigsInfr/refArea;;

            hh1.genderRatioInRange = gender/refArea;;
            hh1.genderRatioInfectedInRange = genderInf/refArea;;
  
            hh1.numHHInRange = hh1.numHHInRange/refArea;

            avgNumInRange = avgNumInRange + hh1.numHHInRange;
        }

        if(simVilla.extendedOutput)System.out.println ("Village: " + simVilla.villageName + " avg num of household in range (range " + simVilla.simW.rangeLimit   +  "): " + avgNumInRange/(double)simVilla.householdsBag.size());

        //System.exit(0);

    }


    //====================================================
    public void getPigsPrevalenceInRange()
    {
        System.out.println (" ");
        System.out.println ("----------------------------------------");
        System.out.println ("Getting pigs prevalence in range");

        //Initialize the households serology data
        for(int i = 0; i < simVilla.householdsBag.size(); i ++)
        {
            Household hh = (Household)simVilla.householdsBag.get(i);

            for(int j = 0; j < 9; j++)
            {
                hh.seropositivePigs.add(0);
                hh.seroSamples.add(0);
                hh.numPigsRound.add(0);
                hh.pigsInRange.add(0);
                hh.pigsInRangePositive.add(0);
            }
        }


        double pigsInRange = 0.0;
        double pigsPositiveInRange = 0.0;

        double pigsPositive = 0.0;
        double pigsPositiveNorm = 0.0;

        int lmin = 0;
        int lmax = 0;

        if(simVilla.simW.simName.equals("TTEMP"))
        {
            lmin = 1;
            lmax = 5;
        }
        if(simVilla.simW.simName.equals("R01"))
        {
            lmin = 1;
            lmax = 8;
        }

        if(simName.equals("R01"))
        {
            lmin = 1;
            lmax = 2;
        }


        //--------------------------------------
        for(int i = 0; i < simVilla.householdsBag.size(); i ++)
        {
            Household hh1 = (Household)simVilla.householdsBag.get(i);

            Point pHome1 = (Point)hh1.mGeometry.getGeometry();


            pigsPositive = 0.0;
            pigsPositiveNorm = 0.0;

            for(int round = lmin; round < lmax; round++)
            {
                for(int cc = 0; cc < simVilla.censedPigsBag.size(); cc++)
                {
                    CensedPig censedP = (CensedPig)simVilla.censedPigsBag. get(cc);
                    //if(censedP.roundsHouseholdsSeroIds.get(round) == 75)censedP.printResume();

                    if(censedP.excluded)continue;
                    if(censedP.roundsCaptured.get(round) != 1)continue;
                    if(censedP.roundsBands.get(round) == -100)continue; 

                    //if(censedP.roundsCaptured.get(round) != 1)continue;

                    //if(censedP.roundsVillages.get(round).equals(simVilla.villageName))continue;
                    if(!censedP.roundsVillages.get(round).equals(simVilla.villageNameNumber))continue;
                    if(censedP.roundsHouseholdsSeroIds.get(round) != hh1.shpId)continue;

                    hh1.numPigsRound.set(round, (1 + hh1.numPigsRound.get(round)));

                    pigsPositiveNorm++;
                    if(censedP.roundsBands.get(round) > 0)
                    {
                        pigsPositive++;
                        hh1.seropositivePigs.set(round, (1 + hh1.seropositivePigs.get(round)));
                    }

                    //censedP.printResume();

                    //System.out.println("numPigsround: " + numPigsRound);
                    //System.out.println("numPigsPositiveround: " + numPigsPositiveRound);
                }
            }

            if(pigsPositiveNorm != 0.0)hh1.pigsSeroprevalence = pigsPositive/pigsPositiveNorm;
            else hh1.pigsSeroprevalence = 0.0;

            //------------------------------------------
            //pigs seropos in range
            pigsInRange = 0;
            pigsPositiveInRange = 0;

            for(int j = 0; j < simVilla.householdsBag.size(); j ++)
            {
                Household hh2 = (Household)simVilla.householdsBag.get(j);

                Point pHome2 = (Point)hh2.mGeometry.getGeometry();

                if(pHome1.distance(pHome2) <= simVilla.simW.rangeLimit)
                {
                    for(int round = lmin; round < lmax; round++)
                    {
                        for(int cc = 0; cc < simVilla.censedPigsBag.size(); cc++)
                        {
                            CensedPig censedP = (CensedPig)simVilla.censedPigsBag. get(cc);

                            if(censedP.excluded)continue;
                            if(censedP.roundsCaptured.get(round) != 1)continue;
                            if(censedP.roundsBands.get(round) == -100)continue; 

                            //if(censedP.roundsVillages.get(round).equals(simVilla.villageName))continue;
                            if(!censedP.roundsVillages.get(round).equals(simVilla.villageNameNumber))continue;
                            if(censedP.roundsHouseholdsSeroIds.get(round) != hh2.shpId)continue;

                            //if(hh1.shpId == 12 && censedP.roundsBands.get(round) > 0 && round == 4)
                            //{
                            //    System.out.println("positive");
                            //    System.out.println("household: " + hh2.shpId);
                            //    censedP.printResume(); 
                            //}

                            pigsInRange++;
                            hh1.pigsInRange.set(round, (1 + hh1.pigsInRange.get(round)));
                            if(censedP.roundsBands.get(round) > 0)
                            {
                                pigsPositiveInRange++;
                                hh1.pigsInRangePositive.set(round, (1 + hh1.pigsInRangePositive.get(round)));
                            }

                            //censedP.printResume();

                            //System.out.println("numPigsround: " + numPigsRound);
                            //System.out.println("numPigsPositiveround: " + numPigsPositiveRound);

                        }
                    }
                }
                //else
                //{
                //    System.out.println("Excluded");
                //}

            }
            if(pigsInRange != 0.0)hh1.pigsSeroprevalenceInRange = pigsPositiveInRange/pigsInRange;
            else hh1.pigsSeroprevalenceInRange = 0.0;

            //System.out.println("--------------------------------");
            //System.out.println("hh: " + hh1.shpId);
            //System.out.println("numPigsround: " + pigsInRange);
            //System.out.println("numPigsPositiveround: " + pigsPositiveInRange);
        }

        //System.exit(0);

        //if(simVilla.extendedOutput)System.out.println ("Village: " + simVilla.villageName + " avg num of household in range (range " + simVilla.simW.rangeLimit   +  "): " + avgNumInRange/(double)simVilla.householdsBag.size());

    }


    //====================================================
    public void getStatsVillagesGlobal()
    {
            //sim.householdsGen.generateHHVoronoi();

            getHHDistancesGlobal();

            getGenderRatioGlobal();

            getHHsValuesInRangeGlobal();

            getPigsPrevalenceInRangeGlobal();
            //System.exit(0);

            calculateHouseholdsCenterPointGlobal();
            //System.exit(0);


            //for(int i = 0; i < censedHouses.size(); i++)
            //{
            //    CensedHousehold chh = (CensedHousehold)censedHouses.get(i);
            //    chh.printResume();
            //}

            int lmin = 0;
            int lmax = 0;

            if(simW.simName.equals("TTEMP"))
            {
                lmin = 4;
                lmax = 5;
            }
            if(simW.simName.equals("R01"))
            {
                lmin = 7;
                lmax = 8;
            }


            if(simName.equals("R01"))
            {
                lmin = 1;
                lmax = 2;
            }



            int numRounds = lmax - lmin;

            //System.exit(0);


            censedHouses = new ArrayList<CensedHousehold>();

            for(int j = 0; j < simW.households.size(); j++)
            {
                Household hh = (Household)simW.households.get(j);

                String name = Integer.toString(hh.shpId);
                CensedHousehold chh = new CensedHousehold(name);

                String simName = simW.simName;
                chh.village = simName + "_" + hh.villageNameNumber;

                chh.shpId = hh.shpId;

                chh.point = hh.geoPoint;

                chh.numHumans = hh.humans.size();
                chh.numInfectedHumans = hh.numInfHumans;

                chh.genderRatio = hh.genderRatio;
                chh.genderRatioInfected = hh.genderRatioInfected;

                for(int round = lmin; round < lmax; round++)
                {
                    chh.numPigs = chh.numPigs + hh.numPigsRound.get(round);
                    chh.seropositivePigs = chh.seropositivePigs + hh.seropositivePigs.get(round);

                    chh.pigsInRange = chh.pigsInRange + hh.pigsInRange.get(round);
                    chh.pigsInRangePositive = chh.pigsInRangePositive + hh.pigsInRangePositive.get(round);

                    //if(chh.name.equals("TTEMP569_12") && round == 4)
                    //{
                    //    System.out.println("-----------------------------");
                    //    System.out.println(chh.pigsInRange);
                    //    System.out.println(chh.pigsInRangePositive);
                    //}

                }

                chh.pigsInRangeRound = hh.pigsInRange;
                chh.pigsInRangePositiveRound = hh.pigsInRangePositive;

                chh.numInfectedPigs = hh.numInfPigs;

                chh.sshhValue = hh.sshhValue;
                chh.sshhValueInRange = hh.sshhValueInRange;
                chh.eliminahecValue = hh.eliminahecValue;
                chh.eliminahecValueInRange = hh.eliminahecValueInRange;

                chh.numPigsInRange = hh.numPigsInRange;
                chh.numHumansInRange = hh.numHumansInRange;

                chh.numInfectedPigsInRange = hh.numInfPigsInRange;
                chh.numInfectedHumansInRange = hh.numInfHumansInRange;

                chh.genderRatioInRange = hh.genderRatioInRange;
                chh.genderRatioInfectedInRange = hh.genderRatioInfectedInRange;


                chh.avgDistFromVillage = hh.avgDistFromVillage;
                chh.distFromVillageCenter = hh.distFromVillageCenter;
                chh.centerDistDecile = hh.CenterDistDecile;
                chh.hhInRange = hh.numHHInRange;


                chh.nElim1InRange = hh.nElim1InRange;
                chh.nSshh4InRange = hh.nSshh4InRange;
                chh.corrCondInRange = hh.corrCondInRange;
                chh.corrMatInRange = hh.corrMatInRange;
                chh.outDefecatorsInRange = hh.outDefecatorsInRange;
                chh.noBathroomInRange = hh.noBathroomInRange;
                chh.scaledEliminahecValueInRange = hh.scaledEliminahecValueInRange;
                chh.scaledSshhValueInRange = hh.scaledSshhValueInRange;
                chh.scaledCorrCondInRange = hh.scaledCorrCondInRange;
                chh.scaledCorrMatInRange = hh.scaledCorrMatInRange;


                if(chh.numPigs !=0)chh.avgPigsSeroprevalence = (double)chh.seropositivePigs/(double)(chh.numPigs * (double)numRounds);
                else chh.avgPigsSeroprevalence = -100.0;
                if(chh.pigsInRange !=0)chh.pigsSeroprevalenceInRange = (double)chh.pigsInRangePositive/(double)(chh.pigsInRange * (double)numRounds);
                else chh.pigsSeroprevalenceInRange = -100.0;

                //chh.printResume();

                censedHouses.add(chh);
            }
    }

    //====================================================
    public void getGenderRatioGlobal()
    {

        int males = 0;
        int females = 0;

        for(int i = 0; i < simW.households.size(); i ++)
        {
            Household hh = (Household)simW.households.get(i);

            for(int j = 0; j < hh.humans.size(); j++)
            {
                Human h = (Human)hh.humans.get(j);

                if(h.gender.equals("male"))males++;
                else females++;
            }

            hh.genderRatio = (double)females/(double)(males + females);

            if(hh.numInfHumans != 0)hh.genderRatioInfected = (double)hh.numInfectedFemale/(double)hh.numInfHumans;
            else hh.genderRatioInfected = 0.0;

        }

    }

    //====================================================
    public void getHHsValuesInRangeGlobal()
    {
        double elim = 0;
        double ssh = 0;
        double pigsr = 0;
        double humansr = 0;
        double pigsInfr = 0;
        double humansInfr = 0;
        
        double gender = 0;
        double genderInf = 0;

        double elim1 = 0;
        double sshh4 = 0;
        double cCorral = 0;
        double mCorral = 0;
        double outDef = 0;
        double noBath = 0;
        double sElim = 0;
        double sSshh = 0;
        double scCorral = 0;
        double smCorral = 0;

        double avgNumInRange = 0.0;

        for(int i = 0; i < simW.households.size(); i ++)
        {
            Household hh1 = (Household)simW.households.get(i);

            Point pHome1 = (Point)hh1.mGeometry.getGeometry();

            elim = 0;
            ssh = 0;
            pigsr = 0;
            humansr = 0;
            pigsInfr = 0;
            humansInfr = 0;

            gender = 0;
            genderInf = 0;

            elim1 = 0;
            sshh4 = 0;
            cCorral = 0;
            mCorral = 0;
            outDef = 0;
            noBath = 0;
            sElim = 0;
            sSshh = 0;
            scCorral = 0;
            smCorral = 0;

            double refArea = Math.PI * Math.PI * simW.rangeLimit * simW.rangeLimit;

            int numHe = 0;
            int numHs = 0;

            for(int j = 0; j < simW.households.size(); j ++)
            {
                Household hh2 = (Household)simW.households.get(j);

                Point pHome2 = (Point)hh2.mGeometry.getGeometry();

                if(pHome1.distance(pHome2) <= simW.rangeLimit)
                {
                    hh1.numHHInRange++;

                    if(hh2.eliminahecValue >= 0)
                    {
                        elim = elim + hh2.eliminahecValue;
                        numHe++;
                        sElim = sElim + hh2.eliminahecValue * hh2.humans.size();

                        if(hh2.eliminahecValue == 1)
                        {
                            elim1++;
                            outDef = outDef + hh2.humans.size();
                        }
                    }
                    if(hh2.sshhValue >= 0)
                    {
                        ssh = ssh + hh2.sshhValue;
                        numHs++;
                        sSshh = sSshh + hh2.sshhValue * hh2.humans.size();

                        if(hh2.sshhValue == 4)
                        {
                            sshh4++;
                            noBath = noBath + hh2.humans.size();
                        }
                    }

                    if(hh2.corralConditionValue > 0)cCorral = cCorral + hh2.corralConditionValue;
                    if(hh2.corralMaterialValue > 0)mCorral = mCorral + hh2.corralMaterialValue;

                    if(hh2.corralConditionValue > 0)scCorral = scCorral + hh2.corralConditionValue * hh2.pigs.size();
                    if(hh2.corralMaterialValue > 0)smCorral = smCorral + hh2.corralMaterialValue * hh2.pigs.size();


                    if(hh2.humans.size() >= 0)humansr = humansr + hh2.humans.size();
                    if(hh2.humans.size() >= 0)humansInfr = humansInfr + hh2.numInfHumans;

                    if(hh2.pigs.size() >= 0)pigsInfr = pigsInfr + hh2.numInfPigs;
                    if(hh2.pigs.size() >= 0)pigsr = pigsr + hh2.pigs.size();

                    if(hh2.genderRatio >= 0)gender = gender + hh2.genderRatio;
                    if(hh2.genderRatioInfected >= 0)genderInf = genderInf + hh2.genderRatioInfected;

                }
            }

            hh1.eliminahecValueInRange = elim/(double)numHe;
            hh1.sshhValueInRange = ssh/numHs;

            hh1.numHumansInRange = humansr/refArea;;
            hh1.numInfHumansInRange = humansInfr/refArea;;

            hh1.numPigsInRange = pigsr/refArea;;
            hh1.numInfPigsInRange = pigsInfr/refArea;;

            hh1.genderRatioInRange = gender/refArea;;
            hh1.genderRatioInfectedInRange = genderInf/refArea;;
  
            hh1.numHHInRange = hh1.numHHInRange/refArea;

            hh1.nElim1InRange = elim1;
            hh1.nSshh4InRange = sshh4;

            hh1.corrCondInRange = cCorral;
            hh1.corrMatInRange = mCorral;

            hh1.outDefecatorsInRange = outDef;
            hh1.noBathroomInRange = noBath;

            hh1.scaledEliminahecValueInRange = sElim;
            hh1.scaledSshhValueInRange = sSshh;

            hh1.scaledCorrCondInRange = cCorral;
            hh1.scaledCorrMatInRange = mCorral;

            avgNumInRange = avgNumInRange + hh1.numHHInRange;
        }

        System.out.println ("Avg num of household in range (range " + simW.rangeLimit   +  "): " + avgNumInRange/(double)simW.households.size());

        //System.exit(0);

    }

    //====================================================
    public void getPigsPrevalenceInRangeGlobal()
    {
        System.out.println (" ");
        System.out.println ("----------------------------------------");
        System.out.println ("Getting pigs prevalence in range");

        //Initialize the households serology data
        for(int i = 0; i < simW.households.size(); i ++)
        {
            Household hh = (Household)simW.households.get(i);

            for(int j = 0; j < 9; j++)
            {
                hh.seropositivePigs.add(0);
                hh.seroSamples.add(0);
                hh.numPigsRound.add(0);
                hh.pigsInRange.add(0);
                hh.pigsInRangePositive.add(0);
            }
        }

        double pigsInRange = 0.0;
        double pigsPositiveInRange = 0.0;

        double pigsPositive = 0.0;
        double pigsPositiveNorm = 0.0;

        int lmin = 0;
        int lmax = 0;

        if(simW.simName.equals("TTEMP"))
        {
            lmin = 1;
            lmax = 5;
        }
        if(simW.simName.equals("R01"))
        {
            lmin = 1;
            lmax = 8;
        }

        if(simName.equals("R01"))
        {
            lmin = 1;
            lmax = 2;
        }



        //--------------------------------------
        System.out.println (" tot num of households: " + simW.households.size());

        for(int i = 0; i < simW.households.size(); i ++)
        {
            Household hh1 = (Household)simW.households.get(i);

            Point pHome1 = (Point)hh1.mGeometry.getGeometry();

            System.out.println ("household: " + hh1.shpId + " num pigs: " + hh1.numPigsRound);

            //System.out.println (hh1.numPigsRound);

            pigsPositive = 0.0;
            pigsPositiveNorm = 0.0;

            for(int round = lmin; round < lmax; round++)
            {
                for(int cc = 0; cc < hh1.censedPigs.size(); cc++)
                {
                    CensedPig censedP = hh1.censedPigs.get(cc);
                    //if(censedP.roundsHouseholdsSeroIds.get(round) == 75)censedP.printResume();

                    if(!censedP.roundsVillages.get(round).equals(hh1.villageNameNumber))continue;
                    if(censedP.roundsHouseholdsSeroIds.get(round) != hh1.shpId)continue;

                    if(censedP.excluded)continue;
                    if(censedP.roundsCaptured.get(round) != 1)continue;
                    if(censedP.roundsBands.get(round) == -100)continue; 

                    //if(censedP.roundsCaptured.get(round) != 1)continue;

                    hh1.numPigsRound.set(round, (1 + hh1.numPigsRound.get(round)));

                    pigsPositiveNorm++;

                    if(censedP.roundsBands.get(round) > 0)
                    {
                        pigsPositive++;
                        hh1.seropositivePigs.set(round, (1 + hh1.seropositivePigs.get(round)));
                    }

                    //censedP.printResume();

                    //System.out.println("numPigsround: " + numPigsRound);
                    //System.out.println("numPigsPositiveround: " + numPigsPositiveRound);
                }
            }


            System.out.println ("Single done");

            if(pigsPositiveNorm != 0.0)hh1.pigsSeroprevalence = pigsPositive/pigsPositiveNorm;
            else hh1.pigsSeroprevalence = 0.0;

            //------------------------------------------
            //pigs seropos in range
            pigsInRange = 0;
            pigsPositiveInRange = 0;

            for(int j = 0; j < simW.households.size(); j ++)
            {
                Household hh2 = (Household)simW.households.get(j);

                Point pHome2 = (Point)hh2.mGeometry.getGeometry();

                if(pHome1.distance(pHome2) <= simW.rangeLimit)
                {
                    for(int round = lmin; round < lmax; round++)
                    {
                        for(int cc = 0; cc < hh2.censedPigs.size(); cc++)
                        {
                            CensedPig censedP = (CensedPig)hh2.censedPigs. get(cc);

                            if(!censedP.roundsVillages.get(round).equals(hh2.villageNameNumber))continue;
                            if(censedP.roundsHouseholdsSeroIds.get(round) != hh2.shpId)continue;

                            if(censedP.excluded)continue;
                            if(censedP.roundsCaptured.get(round) != 1)continue;
                            if(censedP.roundsBands.get(round) == -100)continue; 

                            //if(hh1.shpId == 12 && censedP.roundsBands.get(round) > 0 && round == 4)
                            //{
                            //    System.out.println("positive");
                            //    System.out.println("household: " + hh2.shpId);
                            //    censedP.printResume(); 
                            //}

                            pigsInRange++;
                            hh1.pigsInRange.set(round, (1 + hh1.pigsInRange.get(round)));
                            if(censedP.roundsBands.get(round) > 0)
                            {
                                pigsPositiveInRange++;
                                hh1.pigsInRangePositive.set(round, (1 + hh1.pigsInRangePositive.get(round)));
                            }

                            //censedP.printResume();

                            //System.out.println("numPigsround: " + numPigsRound);
                            //System.out.println("numPigsPositiveround: " + numPigsPositiveRound);

                        }
                    }
                }
                //else
                //{
                //    System.out.println("Excluded");
                //}

            }
            if(pigsInRange != 0.0)hh1.pigsSeroprevalenceInRange = pigsPositiveInRange/pigsInRange;
            else hh1.pigsSeroprevalenceInRange = 0.0;

            //System.out.println("--------------------------------");
            //System.out.println("hh: " + hh1.shpId);
            //System.out.println("numPigsround: " + pigsInRange);
            //System.out.println("numPigsPositiveround: " + pigsPositiveInRange);
        }

        //System.exit(0);
    }



    //====================================================
    public void calculateHouseholdsCenterPointGlobal()
    {
        System.out.println ("Calculating the households distance from village center");

        double x = 0.0;
        double y = 0.0;
        int stats = 0;

        for(int i = 0; i < simW.households.size(); i ++)
        {
            Household hh = (Household)simW.households.get(i);

            Point pHome = (Point)hh.mGeometry.getGeometry();

            x = x + pHome.getX();
            y = y + pHome.getY();
            stats++;
        }

        Coordinate coords  = new Coordinate(x/(double)stats, y/(double)stats);

        GeometryFactory fact =  new GeometryFactory();
        Point point = fact.createPoint(coords);
        villageCenter = point;

        List<Household> houses = new ArrayList<Household>();

        for(int i = 0; i < simW.households.size(); i ++)
        {
            Household hh = (Household)simW.households.get(i);

            Point pHome = (Point)hh.mGeometry.getGeometry();

            hh.distFromVillageCenter = pHome.distance(point);

            houses.add(hh);

            System.out.println ("Household: " + hh.shpId + " distance from village center " + hh.distFromVillageCenter + " m");
        }

        Collections.sort(houses, new DistHouseFromCenterComparator());

        int numDec = simW.numDeciles;
        double delta = 1.0/(double)numDec;


        for(int i = 0; i < houses.size(); i ++)
        {
            Household hh = (Household)houses.get(i);

            //System.out.println (hh.distFromVillageCenter);

            double index = (double)i/(double)houses.size();

            double fracts = 0.0;

            for(int j = 0; j < numDec; j++)
            {
                fracts = fracts + delta;
                if(index <= fracts)
                {
                    hh.CenterDistDecile = j;
                    break;
                }
            }
        }


        //to get households in the first decile
        int numHh = (int)Math.round(0.1 * houses.size());
        //System.out.println ("Num households in the last decile: " + numHh + " num households in the village: " + sim.households.size());

        Boolean goo = true;
        int getIndex = houses.size() - 1;
        stats = 0;
        while(goo)
        {
            Household hh = (Household)houses.get(getIndex);

            hh.inTheBorder = true;

            //hh.printResume();

            //if(sim.extendedOutput)System.out.println ("Household: " + hh.shpId + " distance from village center " + hh.distFromVillageCenter + " m");

            getIndex--;
            stats++;
            if(stats == numHh)goo = false;
        }

        //System.exit(0);

    }





}//End of file

