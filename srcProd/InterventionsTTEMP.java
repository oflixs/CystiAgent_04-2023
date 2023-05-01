/*
   Copyright 2011 by Francesco Pizzitutti
   Licensed under the Academic Free License version 3.0
   See the file "LICENSE" for more information
   */

package sim.app.cystiagents;

import sim.engine.*;
import sim.util.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.*; 
import java.lang.*; 

import java.io.*;
import java.util.*;

import com.vividsolutions.jts.geom.Point;

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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.ChartData;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.charts.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Calendar;
import java.util.Locale;
import static java.util.Calendar.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;  




//----------------------------------------------------
public class InterventionsTTEMP implements Steppable

{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public int interventionsNumStep = 0;

    public int preInterCounter = 0;

    public int runningRound = 1;

    public double intTimeSeroDouble = 0;
    public int intTimeSero = 0;
    public int intTimeSeroMonths = 0;
    public int intCounterSero = 0;
    public Boolean startSero = false;

    public double intTimeDouble = 0;
    public int intTime = 0;
    public int intTimeMonths = 0;
    public int intCounter = 0;
    public Boolean startInt = false;
    public Boolean stopInt = false;

    //time of final intervention after the lastRound
    public int finalIntTimer = 0;
    public int oldFinalIntTimer = 0;

    public List<Double> obsparticipationRounds = new ArrayList<Double>();
    public List<Double> obsparticipationRounds2TreatNoTreat = new ArrayList<Double>();
    public List<Double> obsparticipationRounds2NoTreatTreat = new ArrayList<Double>();

    public List<Double> obsparticipationRoundsRaw = new ArrayList<Double>();
    public List<Double> obsparticipationRounds2TreatNoTreatRaw = new ArrayList<Double>();
    public List<Double> obsparticipationRounds2NoTreatTreatRaw = new ArrayList<Double>();

    public List<Double> nExaminedPigs = new ArrayList<Double>();

    public int statsLineProcessed = 0;

    public List<Integer> statsPart = new ArrayList<Integer>();
    public List<Integer> statsNoPart = new ArrayList<Integer>();

    public List<Integer> stats2TreatNoTreat = new ArrayList<Integer>();
    public List<Integer> stats2NoTreatTreat = new ArrayList<Integer>();

    public double avgPart = 0.0;
    public double avgProbTreatNoTreat = 0.0;
    public double avgProbNoTreatTreat = 0.0;

    public int weeksDiffFinalRound = 0;
    public int statsWeeksDiffFinalRound = 0;

    public List<Double> pigTreatPartRounds = new ArrayList<Double>();

    public double ringRadius = 0.0;

    public int numInterventionsDone = 1;
    public int newZeroStep = 0;

    public int oldnPrint = 0;
    public int oldpreInterv = 0;

    public HSSFWorkbook workbookOutStats = null;
    public String statsSheet = "Interv. stats"; 

    public int totNumPigsExamined = 0;
    public int proptotNumPigsExamined = 0;

    //stats about sims
    public List<Double> numPigsTonguePositive = new ArrayList<Double>();
    public List<Double> propnumPigsTonguePositive = new ArrayList<Double>();

    public List<Double> numRings = new ArrayList<Double>();

    public List<Double> numEligibleHousesRing = new ArrayList<Double>();
    public List<Double> propnumEligibleHousesRing = new ArrayList<Double>();

    public List<Double> numEligibleParticipantsRing = new ArrayList<Double>();
    public List<Double> propnumEligibleParticipantsRing = new ArrayList<Double>();

    //for ring treatment
    public List<Double> numTreated1DoseRing = new ArrayList<Double>();
    public List<Double> numTreated2DoseRing = new ArrayList<Double>();

    public List<Double> propTreated1DoseRing = new ArrayList<Double>();
    public List<Double> propTreated2DoseRing = new ArrayList<Double>();

    public List<Double> obsnumTreated1DoseRing = new ArrayList<Double>();
    public List<Double> obsnumTreated2DoseRing = new ArrayList<Double>();

    public List<Double> obspropTreated1DoseRing = new ArrayList<Double>();
    public List<Double> obspropTreated2DoseRing = new ArrayList<Double>();



    public List<Double> propEffTreatments = new ArrayList<Double>();

    //for ring screening
    public List<Double> numProvidedStools = new ArrayList<Double>();
    public List<Double> propProvidedStools = new ArrayList<Double>();

    public List<Double> numDetectedTapeworms = new ArrayList<Double>();
    public List<Double> propDetectedTapeworms = new ArrayList<Double>();

    public List<Double> numTreatPigs = new ArrayList<Double>();
    public List<Double> propTreatPigs = new ArrayList<Double>();

    public double obsPrevHTFinal = 0.0;
    public double obsPrevHTFinalNorm = 0.0;

    public int numWeeksInt = -1;

    public HashMap<Integer, List<List<String>>> housesLinesMap = new HashMap<Integer, List<List<String>>>();
    public List<List<String>> houseLines = new ArrayList<List<String>>();

    public HashMap<String, List<Calendar>> interventionsDateInd = new HashMap<String, List<Calendar>>();

    public List<Object[]> intXlsData = new ArrayList<Object[]>();
    public int intXlsDataNorm = 0;

    public double perpigletsSeroPrevalenceSim = 0.0;
    public double perpigletsSeroPrevalenceSimNorm = 0.0;
    public double perSeroPrevalenceSimAgeTransition = 0.0;
    public double perSeroPrevalenceSimAgeTransitionNorm = 0.0;
    public double perpigsYoungSeroPrevalenceSim = 0.0;
    public double perpigsYoungSeroPrevalenceSimNorm = 0.0;
    public double perpigsAdultSeroPrevalenceSim = 0.0;
    public double perpigsAdultSeroPrevalenceSimNorm = 0.0;




    //====================================================
    public InterventionsTTEMP(SimState pstate)
    {
        //this is to simulate the same time scheduling of
        //pigs seroscreening of R01 interventions
        //for TTEMP villages
        //
        //if(sim.extendedOutput)System.out.println("---- New MeatPortion");

        state = pstate;
        sim = (CystiAgents)state;

        sim.schedule.scheduleRepeating(1.0, 14, this);

        //R01 interventions ------------------
        //reads participation file to associate villages to intervention arms
        //sim.pigsGenR01.readInterventionsArms();

        //time interval between two pig serology sampling for R01 is always 4 months
        intTimeSeroDouble = (sim.weeksInAMonth * 4);
        intTimeSero = (int)Math.round(sim.weeksInAMonth * 4);
        intTimeSeroMonths = 4;

        //number of sero sampling rounds for R01
        sim.seroNRounds = 7;

        sim.village.interventionType = "TTEMP";

        //setting the time between two rounds of intervention
        if(sim.village.interventionType.equals("TTEMP")
          )
        {
            intTimeDouble = (sim.weeksInAMonth * 4);
            intTime = (int)Math.round(sim.weeksInAMonth * 4);
            intTimeMonths = 4;
            //num rounds 
            sim.intRounds = 7;
        }

        for(int i = 0; i < 9; i++)
        {
            pigTreatPartRounds.add(0.0);

            numPigsTonguePositive.add(0.0);
            propnumPigsTonguePositive.add(0.0);

            numRings.add(0.0);

            numEligibleParticipantsRing.add(0.0);
            propnumEligibleParticipantsRing.add(0.0);

            numEligibleHousesRing.add(0.0);
            propnumEligibleHousesRing.add(0.0);

            numTreated1DoseRing.add(0.0);
            numTreated2DoseRing.add(0.0);

            propTreated1DoseRing.add(0.0);
            propTreated2DoseRing.add(0.0);

            obsnumTreated1DoseRing.add(0.0);
            obsnumTreated2DoseRing.add(0.0);

            obspropTreated1DoseRing.add(0.0);
            obspropTreated2DoseRing.add(0.0);

            propEffTreatments.add(0.0);

            numProvidedStools.add(0.0);
            propProvidedStools.add(0.0);

            numDetectedTapeworms.add(0.0);
            propDetectedTapeworms.add(0.0);

            numTreatPigs.add(0.0);
            propTreatPigs.add(0.0);

            sim.obsSeroPrevalencePigletsRounds.add(0.0);
            sim.obsSeroPrevalencePigsRounds.add(0.0);
            sim.obsSeroIncidencePigsRounds.add(0.0);

            sim.obsPrevTonguePositiveRounds.add(0.0);

        }

        //if(sim.extendedOutput)System.out.println(intTime);
        //System.exit(0);

        //Read interventions data to get perticipation rates and 
        //timing of interventions
        //readHumansInterventionsData();
        //System.exit(0);
        //Read Pig participation file
        readPigParticipation();
        //System.exit(0);

        //System.exit(0);

        //readBaselineFiles();
        //readFinalRoundFiles();
        //readMidRoundFiles();


        //change the simulation numStep to almost infinite 
        //(this will be adjusted to 0 after the intervention 
        //ends see step method)
        sim.numStep = 1000000000;

        if(sim.extendedOutput)System.out.println(sim.villageName + " -----------------------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Intervention type: " + sim.village.interventionType);

        //if(sim.village.R01InterventionArm == 1)sim.village.interventionType = "Ring Scr (P)";
        //if(sim.village.R01InterventionArm == 2)sim.village.interventionType = "Ring Scr";
        //if(sim.village.R01InterventionArm == 3)sim.village.interventionType = "Ring Trt (P)";
        //if(sim.village.R01InterventionArm == 4)sim.village.interventionType = "Ring Trt";
        //if(sim.village.R01InterventionArm == 5)sim.village.interventionType = "Mass Trt";
        //if(sim.village.R01InterventionArm == 6)sim.village.interventionType = "Mass Trt (P)";

        //System.exit(0);

        //interventionsNumStep = 200;

        sim.postInterventionsNumStep = sim.postInterventionsNumStep + oldFinalIntTimer;
        //System.exit(0);


        //sim.numStep = sim.preInterventionsNumStep + interventionsNumStep;

        //if(sim.extendedOutput)System.out.println(sim.villageName + " Interventions numSteps: " + sim.numStep);

        //String outFile = sim.outDirSims;
        //File file = new File(outFile);
        //if(file.exists())writeInterventionsXls();

        //ringRadius = sim.ringSize/sim.geoCellSize;
        ringRadius = sim.ringSize;
        if(sim.extendedOutput)System.out.println(sim.villageName + " ring radius: " + sim.ringSize + " m, ring size in cellS: " + ringRadius); 
        //
        // System.exit(0);

        for(int i = 0; i < (sim.seroNRounds + 1); i++)
        {
            sim.interSeroIncidencePigsRounds.add(0.0);
            sim.interSeroPrevalencePigsRounds.add(0.0);
            sim.interSeroPrevalencePigsCohortsRounds.add(0.0);
            sim.interSeroPrevalencePigletsRounds.add(0.0);

            sim.avgInterSeroIncidencePigsRounds.add(0.0);
            sim.avgInterSeroPrevalencePigsRounds.add(0.0);
            sim.avgInterSeroPrevalencePigsCohortsRounds.add(0.0);
            sim.avgInterSeroPrevalencePigletsRounds.add(0.0);

            sim.avgInterPrevTonguePositiveRounds.add(0.0);
        }

        oldnPrint = sim.nPrint;
        oldpreInterv = sim.preInterventionsNumStep;

        /*
        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household h1 = (Household)sim.householdsBag.get(i);
            CoverPixel cp1 = h1.cpPosition;

            for(int j = (i + 1); j < sim.householdsBag.size(); j++)
            {
                Household h2 = (Household)sim.householdsBag.get(j);
                CoverPixel cp2 = h2.cpPosition;

                double dist = (cp1.xcor - cp2.xcor) * (cp1.xcor - cp2.xcor);
                dist = dist + (cp1.ycor - cp2.ycor) * (cp1.ycor - cp2.ycor);
                dist = Math.sqrt(dist);
                dist = dist * sim.geoCellSize;

                if(sim.extendedOutput)System.out.println("dist houses " + h1.shpId + " - " + h2.shpId + " = " + dist);

            }
        }
        */

        workbookOutStats = new HSSFWorkbook();

        //if(sim.village.interventionType.equals("Ring Trt")
        //        ||  sim.village.interventionType.equals("Ring Trt (P)")
        //        ||  sim.village.interventionType.equals("Ring Scr")
        //        ||  sim.village.interventionType.equals("Ring Scr (P)")
        //  )
        //{
        //    writeObservedIntersRing();
        //}

        writeObservedIntersRingStats();

        //initialize the list of outputs
        sim.statistics.initXlsData();

        //setEfficacies();

        //System.exit(0);
    }

    //====================================================
    public void step(SimState pstate)
    {
        //eraseInfections();

        int now = (int)state.schedule.getTime();  

        //change the output print frequency to 1 print per step
        if(!sim.burnin)
        {
            sim.nPrint = 1;
            //sim.baselineTnPrev = 0.0;
        }

        if(((now % sim.nPrint) == 0 || sim.nPrint == 1) && !sim.burnin)
        {
            sim.statistics.calculateSeroStats();
            sim.statistics.writeXlsOutput();
            if(sim.extendedOutput)System.out.println("Writing to Xls output");
            //System.exit(0);
        }



        //if(sim.extendedOutput)System.out.println(finalIntTimer + " " + oldFinalIntTimer);

        if(!sim.burnin 
                && (now == (sim.burninPeriod + sim.preInterventionsNumStep))
                && !stopInt
                )
        {

            if(sim.extendedOutput)System.out.println("");
            if(sim.extendedOutput)System.out.println(sim.villageName + " -----------------------------------------");
            if(sim.extendedOutput)System.out.println(sim.villageName + " =========================================");
            String time = getTime(now, "simulation");
            if(sim.extendedOutput)System.out.println(sim.villageName + " TTEMP " + sim.village.interventionType  + " interventions are starting now:" + time);
            if(sim.extendedOutput)System.out.println(sim.villageName + " Num of intervention cycles done: " + (numInterventionsDone - 1));

            startInt  = true;
            startSero = true;
            intCounterSero = 0;
        }

        if(runningRound > (sim.intRounds) && !stopInt)
        {
            stopInt = true;
            startInt = false;
            intCounter = 0;
        }

        if(startInt && !stopInt)
        {
            //if(sim.extendedOutput)System.out.println(intCounter + " " + intTime + " " + (intCounter % intTime));

            //int nMonths = runningRound * intTimeMonths;
            //int nWeeks = (int)Math.round(sim.weeksInAMonth * nMonths);

            //if(sim.extendedOutput)System.out.println("int: " + ((double)intCounter % (double)intTime) + " " + ((double)intCounter / (double)intTime));

            //if(sim.extendedOutput)System.out.println("double: " + ((double)intCounter % (double)intTimeDouble) + " " + ((double)intCounter / (double)intTimeDoubleDouble));

            if(Math.round((intCounter % intTimeDouble)) == 0)
            {
                runningRound++;
            }
            else
            {
                sim.interventionStep = 0;
            }

            intCounter++;
        }

        if(stopInt && !startInt)
        {
            intCounter++;
            if(intCounter == sim.postInterventionsNumStep
                    && numInterventionsDone == sim.interventionRepetitions)
            {
                stopSim();
            }
            else if(intCounter == sim.postInterventionsNumStep
                    && numInterventionsDone != sim.interventionRepetitions)
            {
                resetSim();
                numInterventionsDone++;
            }

            if(finalIntTimer == 0)
            {
                sim.interventionStep = 1;

                getFinalHTandPC();
            }
            else
            {
                sim.interventionStep = 0;
            }

            finalIntTimer--;
        }

        //---- Seroincidence measurements ----
        if(startSero)
        {
            sim.writeIntSero = false;

            if(Math.round(intCounterSero % intTimeSeroDouble) == 0)
            {
                sim.roundSero++;

                if(sim.roundSero <= sim.seroNRounds)
                {

                    if(sim.extendedOutput)System.out.println(sim.villageName + " ");
                    if(sim.extendedOutput)System.out.println(sim.villageName + " .........................................");
                    if(sim.extendedOutput)System.out.println(sim.villageName + " Pig serology measurements round: " + sim.roundSero);
                    String time = getTime(intCounterSero, "serology measurements");
                    if(sim.extendedOutput)System.out.println(sim.villageName + time);
                    time = getTime((intCounter - 1), "interventions");
                    if(sim.extendedOutput)System.out.println(sim.villageName + time);

                    sim.serosampleDone = 1;
                    //if(sim.extendedOutput)System.out.println("cccccccccccccccccccccccccccccccccccc");
                    //if(sim.extendedOutput)System.out.println(sim.roundSero);
                    addPigletsToSeroIncCohort();
                    calculateSeroStats();

                    if(sim.roundSero == 1)calculatePerSeroPrevalencesnPrint();

                    removePigsFromSeroIncCohort();
                    sim.writeIntSero = true;

                    if(sim.extendedOutput)System.out.println(sim.villageName + " .........................................");

               }
            }
            else
            {
                sim.serosampleDone = 0;
            }

            //if(sim.extendedOutput)System.out.println("2 " + intCounterSero + " " + intTimeSero);
            intCounterSero++;
            //if(sim.extendedOutput)System.out.println("3 " + intCounterSero + " " + intTimeSero);

        }

        //System.exit(0);
    }

    //====================================================
    public void calculateSeroStats()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " .... Calculating pigs serology stats");

        double pigsSeroprev = 0;
        double pigsSeroprevStats = 0;
        double pigsCohortsSeroprev = 0;
        double pigsCohortsSeroprevStats = 0;

        double pigletsSeroprev = 0;
        double pigletsSeroprevStats = 0;
        double pigsSeroinc = 0;
        double pigsSeroincStats = 0;

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(pig.imported)continue;

            if(!pig.isAPiglet())
            {
                if(pig.seropositive)pigsSeroprev++;
                pigsSeroprevStats++;
            }

            if(!pig.isAPiglet()
            && pig.isInTheSeroIncCohortInt)
            {
                if(pig.seropositive)pigsCohortsSeroprev++;
                pigsCohortsSeroprevStats++;
            }


            if(pig.isAPigletSeroInc())
            {
                if(pig.seropositive)pigletsSeroprev++;
                pigletsSeroprevStats++;
            }

            if(pig.isInTheSeroIncCohortInt)
            {
                if(pig.seropositive)pigsSeroinc++;
                pigsSeroincStats++;
            }
        }

        //sim.seroPrevalencePigsInter = (double)pigsSeroprev/(double)pigsSeroprevStats;
        //sim.seroPrevalencePigletsInter = (double)pigletsSeroprev/(double)pigletsSeroprevStats;
        //sim.seroIncidencePigsInter = (double)pigsSeroinc/(double)pigsSeroincStats;



        sim.interSeroPrevalencePigsRounds.set(sim.roundSero, (double)pigsSeroprev/(double)pigsSeroprevStats);
        sim.interSeroPrevalencePigsCohortsRounds.set(sim.roundSero, (double)pigsCohortsSeroprev/(double)pigsCohortsSeroprevStats);
        sim.interSeroPrevalencePigletsRounds.set(sim.roundSero, (double)pigletsSeroprev/(double)pigletsSeroprevStats);
        sim.interSeroIncidencePigsRounds.set(sim.roundSero, (double)pigsSeroinc/(double)pigsSeroincStats);

        //if(sim.extendedOutput)System.out.println("pigsSeroinc " + pigsSeroinc);
        //if(sim.extendedOutput)System.out.println("pigsSeroincStats " + pigsSeroincStats);

        //int now = (int)state.schedule.getTime();
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Interventions <<<<<<<<<<<<<<<<<<");
        //System.out.println("now: " + now);
        //System.out.println("piglets norm: " + pigletsSeroprevStats);
        //System.out.println("piglets num seropos: " + pigletsSeroprev);


        double newValue = sim.interSeroPrevalencePigsRounds.get(sim.roundSero) + sim.avgInterSeroPrevalencePigsRounds.get(sim.roundSero);
        //if(sim.extendedOutput)System.out.println(newValue);
        sim.avgInterSeroPrevalencePigsRounds.set(sim.roundSero, newValue);

        newValue = sim.interSeroPrevalencePigsCohortsRounds.get(sim.roundSero) + sim.avgInterSeroPrevalencePigsCohortsRounds.get(sim.roundSero);
        //if(sim.extendedOutput)System.out.println(newValue);
        sim.avgInterSeroPrevalencePigsCohortsRounds.set(sim.roundSero, newValue);



        newValue = sim.interSeroPrevalencePigletsRounds.get(sim.roundSero) + sim.avgInterSeroPrevalencePigletsRounds.get(sim.roundSero);
        sim.avgInterSeroPrevalencePigletsRounds.set(sim.roundSero, (newValue));

        newValue = sim.interSeroIncidencePigsRounds.get(sim.roundSero) + sim.avgInterSeroIncidencePigsRounds.get(sim.roundSero);
        sim.avgInterSeroIncidencePigsRounds.set(sim.roundSero, (newValue));

        if(sim.extendedOutput)System.out.println(sim.villageName + " .... Serostats round: " + sim.roundSero + " .....");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Piglets seroprevalence: " + sim.interSeroPrevalencePigletsRounds.get(sim.roundSero));
        if(sim.extendedOutput)System.out.println(sim.villageName + " Avg. Piglets seroprevalence: " + sim.avgInterSeroPrevalencePigletsRounds.get(sim.roundSero)/(double)numInterventionsDone);

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs seroprevalence: " + sim.interSeroPrevalencePigsRounds.get(sim.roundSero));
        if(sim.extendedOutput)System.out.println(sim.villageName + " Avg. Pigs seroprevalence: " + sim.avgInterSeroPrevalencePigsRounds.get(sim.roundSero)/(double)numInterventionsDone);

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs Cohorts seroprevalence: " + sim.interSeroPrevalencePigsCohortsRounds.get(sim.roundSero));
        if(sim.extendedOutput)System.out.println(sim.villageName + " Avg. Pigs Cohorts seroprevalence: " + sim.avgInterSeroPrevalencePigsCohortsRounds.get(sim.roundSero)/(double)numInterventionsDone);

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Seroincidence: " + sim.interSeroIncidencePigsRounds.get(sim.roundSero));
        if(sim.extendedOutput)System.out.println(sim.villageName + " Avg. Seroincidence: " + sim.avgInterSeroIncidencePigsRounds.get(sim.roundSero)/(double)numInterventionsDone);

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Seroincidence: " + sim.interSeroIncidencePigsRounds.get(sim.roundSero));
        if(sim.extendedOutput)System.out.println(sim.villageName + " Avg. Seroincidence: " + sim.avgInterSeroIncidencePigsRounds.get(sim.roundSero)/(double)numInterventionsDone);



    }

    //====================================================
    public void addPigletsToSeroIncCohort()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " ---- Adding piglets to incidence cohort");

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);
            if(sim.roundSero == 1)pig.isInTheSeroIncCohortInt = false;

            if(pig.isAPigletSeroInc())
            {
                pig.isInTheSeroIncCohortInt = true;
            }

        }
    }

    //====================================================
    public void removePigsFromSeroIncCohort()
    {
        //if(sim.extendedOutput)System.out.println(sim.villageName + " ---- Removing pigs from incidence cohort");

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(!pig.isAPiglet())
            {
                if(pig.isInTheSeroIncCohortInt)
                {
                    if(pig.seropositive)pig.isInTheSeroIncCohortInt = false;
                }
            }
        }
    }



    //====================================================
    public void generateSeroIncCohort()
    {
        if(sim.extendedOutput)System.out.println("---- Upgrading pigs incidence cohort");

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(pig.isInTheSeroIncCohortInt)
            {
                if(pig.seropositive)pig.isInTheSeroIncCohortInt = false;
            }

            if(pig.isAPigletSeroInc())
            {
                pig.isInTheSeroIncCohortInt = true;
            }

        }
    }

    //====================================================
    public void stopSim()
    {
        int now = (int)sim.schedule.getTime();

        sim.numStep = now + 5;


        //------------------------------------------------
        //write the accumulated stats data variable to main data variable
        //for outputs writings

        accumulateStatsOut();

        for(int i = 0; i < intXlsData.size(); i++)
        {
            if(i == 0)
            {
                //titles = (Object)sim.weeklyData.get(i);
                continue;
            }

            Object[] objData = (Object[])intXlsData.get(i);

            for(int j = 1; j < objData.length; j++)
            {
                objData[j] = (Object)((double)objData[j]/intXlsDataNorm);

                //System.out.println(objData[j]);
            }

            intXlsData.set(i, (Object[])objData);

        }

        //sim.weeklyData = intXlsData;
        sim.weeklyData = new ArrayList<Object[]>(intXlsData);

        sim.statistics.writeXlsOutput();


        //writeSeroInctoXls(workbookOutStats);
        //writeSeroInctoXlsForExtensions();
        //writeFinalHTXls();
        //createGraphSeroInc();
    }

    //====================================================
    public void resetSim()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " =========================================");
        if(sim.extendedOutput)System.out.println(sim.villageName + " =========================================");
        if(sim.extendedOutput)System.out.println(sim.villageName + " -----------------------------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Resetting the simulation to begin another intervention trial");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Trial number: " + numInterventionsDone);

        //read the village picture 
        sim.village.readVillageFromFile();
        if(sim.extendedOutput)sim.householdsGen.printHouseholdsStats();

        int now = (int)sim.schedule.getTime();
        newZeroStep = now;

        //set a new bornin period to reequilibrate the simulation
        sim.burnin = true;
        sim.burninPeriodCounter = 0;
        sim.nPrint = oldnPrint;

        sim.preInterventionsNumStep = oldpreInterv + newZeroStep;

        runningRound = 1;
        sim.roundSero = 0;

        //reset the stoppers
        startInt = false;
        startSero = false;

        stopInt = false;

        intCounterSero = 0;
        intCounter = 0;

        finalIntTimer = oldFinalIntTimer;

        sim.statistics.resetStats();

        accumulateStatsOut();

    }



    //====================================================
    //count humans in range ring strategy around a pig
    public Bag getHumansInRangePig(Pig pig)
    {
        int stats = 0;
        Bag bag = new Bag();
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human human = (Human)sim.humansBag.get(i);

            //if(human.traveling)contiue;
            if(human.traveling || human.strangerTraveler)continue;

            //CoverPixel cp = human.cpPosition;

            //double dist = (cp.xcor - pig.cpPosition.xcor) * (cp.xcor - pig.cpPosition.xcor);
            //dist = dist + (cp.ycor - pig.cpPosition.ycor) * (cp.ycor - pig.cpPosition.ycor);
            //dist = Math.sqrt(dist);

            Point hPoint = human.household.geoPoint;
            Point pPoint = pig.household.geoPoint;

            //double dist = (cp.xcor - pig.cpPosition.xcor) * (cp.xcor - pig.cpPosition.xcor);
            //dist = dist + (cp.ycor - pig.cpPosition.ycor) * (cp.ycor - pig.cpPosition.ycor);

            //double dist = (hPoint.getX() - pPoint.getX()) * (hPoint.getX() - pPoint.getX());
            //dist = dist + (hPoint.getY() - pPoint.getY()) * (hPoint.getY() - pPoint.getY());
            //dist = Math.sqrt(dist);

            double dist = hPoint.distance(pPoint);

            if(dist <= ringRadius)
            {
                stats++;
                bag.add(human);
                //if(sim.extendedOutput)System.out.println("human household shpId: " + human.household.shpId);
                //human.printResume();
            }
        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " " + stats  + " humans inside the ring");


        return bag;
    }

    //====================================================
    //count pigs in range ring strategy around a pig
    public Bag getPigsInRangePig(Pig pigRef)
    {
        int stats = 0;
        Bag bag = new Bag();

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);
            //CoverPixel cp = pig.cpPosition;

            Point refPoint = pigRef.household.geoPoint;
            Point pPoint = pig.household.geoPoint;

            //double dist = (cp.xcor - pig.cpPosition.xcor) * (cp.xcor - pig.cpPosition.xcor);
            //dist = dist + (cp.ycor - pig.cpPosition.ycor) * (cp.ycor - pig.cpPosition.ycor);

            //double dist = (refPoint.getX() - pPoint.getX()) * (refPoint.getX() - pPoint.getX());
            //dist = dist + (refPoint.getY() - pPoint.getY()) * (refPoint.getY() - pPoint.getY());
            //dist = Math.sqrt(dist);

            //double dist = (cp.xcor - pigRef.cpPosition.xcor) * (cp.xcor - pigRef.cpPosition.xcor);
            //dist = dist + (cp.ycor - pigRef.cpPosition.ycor) * (cp.ycor - pigRef.cpPosition.ycor);
            //dist = Math.sqrt(dist);

            //dist = dist * sim.geoCellSize;

            double dist = refPoint.distance(pPoint);

            if(dist <= ringRadius)
            {
                stats++;
                bag.add(pig);
            }
        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " " + stats  + " pigs inside the ring");

        return bag;
    }

    //====================================================
    public void massTreatFinal()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " =========================================");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Humans Mass Treatment final week: " + sim.numWeeks);
        //System.exit(0);

        int stats = 0;
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            //if(h.traveling)continue;
            if(h.traveling || h.strangerTraveler)continue;

            double rand = state.random.nextDouble();
            
            //if(rand < sim.treatFinalPart)
            if(rand < sim.niclosamideTreatEff)
            {
                if(!h.tapeworm)continue;

                rand = state.random.nextDouble();
                if(rand < sim.screenTrtEff)
                {
                    stats++;
                    h.treat();
                }
            }
        }
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num humans treated: " + stats);
        //System.exit(0);
    }

    //====================================================
    public void massScreenH(double screenPart)
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " Mass screening of Humans week: " + sim.numWeeks);
        //System.exit(0);

        int stats = 0;
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            //if(h.traveling)continue;
            if(h.traveling || h.strangerTraveler)continue;

            double rand = state.random.nextDouble();
            
            if(rand < screenPart)
            {
                if(!h.tapeworm)continue;

                rand = state.random.nextDouble();
                //if(rand < sim.treat1Eff)
                if(rand < sim.niclosamideTreatEff)
                {
                    stats++;
                    h.treat();
                }

                rand = state.random.nextDouble();
                if(rand < sim.elisaSens)
                {
                   //h.screenPos = true; 
                }

            }
        }
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num humans treated: " + stats);
        //System.exit(0);
    }

    //====================================================
    public void massScreenTreatH()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " Mass screening treatment of Humans week: " + sim.numWeeks);
        //System.exit(0);

        int stats = 0;
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            //if(h.traveling)continue;
            if(h.traveling || h.strangerTraveler)continue;
            //if(!h.screenPos)continue;

            double rand = state.random.nextDouble();
            
            if(rand < sim.screenTrtEff)
            {
                stats++;
                if(!h.tapeworm)continue;

                h.treat();

            }
        }
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num humans treated: " + stats);
        //System.exit(0);
    }


    //====================================================
    public void massTreatP()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " =========================================");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Pigs Mass treatment round: " + sim.roundSero);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Treatment participation: " + pigTreatPartRounds.get(sim.roundSero));
        String time = getTime(intCounter, "interventions");
        if(sim.extendedOutput)System.out.println(sim.villageName + time);

        int statsTreat = 0;
        int statsTreatNorm = 0;

        for(int i = 1; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);
            
            if(pig.age < sim.pigAgeEligibleForTreatment)continue;


            double rand = state.random.nextDouble();

            if(rand < pigTreatPartRounds.get(sim.roundSero))
            {
                statsTreat++;
                pig.treatOXF(); 
            }
        }

        if(sim.extendedOutput)System.out.println("Num pigs treated: " + statsTreat);

        double newValue = numTreatPigs.get(sim.roundSero) + (double)statsTreat;
        numTreatPigs.set(sim.roundSero, newValue);

        newValue = propTreatPigs.get(sim.roundSero) + (double)statsTreat/(double)statsTreatNorm;
        propTreatPigs.set(sim.roundSero, newValue);;
    }





    //====================================================
    public void massTreatH(String what)
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " =========================================");
        if(what.equals("final"))if(sim.extendedOutput)System.out.println(sim.villageName + " Humans Mass treatment final round");
        else if(sim.extendedOutput)System.out.println(sim.villageName + " Humans Mass treatment round " + runningRound);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Humans Mass treatment");
        String time = getTime(intCounter, "interventions");
        if(sim.extendedOutput)System.out.println(sim.villageName + time);
        //System.exit(0);

        double treatPart = 0.0;
        double treatEff = 0.0;

        treatPart = obsparticipationRounds.get(runningRound);
        if(sim.extendedOutput)System.out.println("Treatment participation: " + treatPart);

        if(what.equals("round"))
        {
            //treatEff = sim.//treat1Eff;
            treatEff = sim.niclosamideTreatEff;
        }
        if(what.equals("final"))
        {
            //treatEff = sim.screenTrtEff;
            treatEff = sim.niclosamideTreatEff;
        }

        if(sim.extendedOutput)System.out.println("Treatment efficacy: " + treatEff);
        //if()

        int statsTreat = 0;
        int statsEff = 0;
        int statsTreatNorm = 0;
        int statsEffNorm = 0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            statsTreatNorm++;

            //if(h.traveling || h.strangerTraveler)continue;
            //if(h.traveling)continue;
            if(h.traveling || h.strangerTraveler)continue;

            if(h.age < (2 * sim.weeksInAYear))continue;

            double rand = state.random.nextDouble();

            if(rand < treatPart)
            {
                statsTreat++;
                statsEffNorm++;
                rand = state.random.nextDouble();

                if(rand < treatEff)
                {
                    statsEff++;

                    h.treat();
                }
            }
        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " Num humans treated: " + statsTreat);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num humans effective treatments: " + statsEff);

        double newValue = numTreated1DoseRing.get(runningRound) + (double)statsTreat;
        numTreated1DoseRing.set(runningRound, newValue);

        newValue = propTreated1DoseRing.get(runningRound) + (double)statsTreat/statsTreatNorm;
        propTreated1DoseRing.set(runningRound, newValue);

        //System.exit(0);
    }

    //====================================================
    public void ringScreenH()
    {
        if(sim.extendedOutput)System.out.println("");
        if(sim.extendedOutput)System.out.println(sim.villageName + " -----------------------------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Ring Screen Humans round: " + runningRound);
        String time = getTime(intCounter, "interventions");
        if(sim.extendedOutput)System.out.println(sim.villageName + time);

        int statsTreat = 0;
        int statsTreatNorm = 0;
        int statsDetect = 0;
        int statsDetectNorm = 0;
        int statsEffTreat = 0;
        int statsEffTreatNorm = 0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            statsTreatNorm++;

            if(!h.eligibleH)continue;

            double rand = state.random.nextDouble();

            if(rand < avgPart)
            {
                if(!h.tapeworm)continue;

                statsTreat++;
                statsDetectNorm++;

                rand = state.random.nextDouble();
                if(rand < sim.elisaSens)
                {
                    statsDetect++;

                    if(h.age < (2 * sim.weeksInAYear))continue;

                    statsEffTreatNorm++;

                    rand = state.random.nextDouble();

                    //if(rand < sim.treat1Eff)
                    if(rand < sim.niclosamideTreatEff)
                    {
                        statsEffTreat++;

                        h.treat();
                    }

                    h.markedForRetreatment = true;
                    h.reTreatTimer = 4;
                }
            }

            h.eligibleH = false;
        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " Num tapeworm carriers inside the rings this round " + statsTreat);
        if(sim.extendedOutput)System.out.println(sim.villageName  + " Num tapeworm carriers inside the rings detected by the screening this round: " + statsDetect);

        double newValue = propEffTreatments.get(runningRound) + (double)statsEffTreat/(double)statsEffTreatNorm;
        propEffTreatments.set(runningRound, newValue);

        newValue = numProvidedStools.get(runningRound) + (double)statsTreat;
        numProvidedStools.set(runningRound, newValue);

        newValue = propProvidedStools.get(runningRound) + (double)statsTreat/(double)statsTreatNorm;
        propProvidedStools.set(runningRound, newValue);

        newValue = numDetectedTapeworms.get(runningRound) + (double)statsDetect;
        numDetectedTapeworms.set(runningRound, newValue);

        newValue = propDetectedTapeworms.get(runningRound) + (double)statsDetect/(double)statsDetectNorm;
        propDetectedTapeworms.set(runningRound, newValue);
    
    }

    //====================================================
    public void ringScreenTreatH()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " Ring Screen Treat Humans week: " + sim.numWeeks);
        int stats = 0;
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            //if(!h.screenPos)continue;
            //if(!h.eligibleH)continue;
            if(!h.tapeworm)continue;

            double rand = state.random.nextDouble();

            if(rand < sim.screenTrtEff)
            {
                h.treat();
            }

            //h.screenPos = false;
        }
    
    }

    //====================================================
    public void ringTreatH()
    {
        if(sim.extendedOutput)System.out.println("");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Ring treatment Humans");

        if(sim.extendedOutput)System.out.println("Average Participation first dose: " + avgPart);
        if(sim.extendedOutput)System.out.println("Average Avg. Prob. Treated No Treated: " + avgProbTreatNoTreat);
        if(sim.extendedOutput)System.out.println("Average Avg. Prob. No Treated Treated: " + avgProbNoTreatTreat);

        int statsElig = 0;
        int statsTreat1 = 0;//one dose
        int statsTreat2 = 0;//two doses

        int statsEffTreat = 0;
        int statsEffTreatNorm = 0;

        Boolean wasTreated = false;

        List<Human> humans1Dose = new ArrayList<Human>();
        List<Human> humans2Doses = new ArrayList<Human>();

        double rand = 0.0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            wasTreated= false;

            if(h.age < (2 * sim.weeksInAYear))continue;
            if(!h.eligibleH)continue;
            h.eligibleH = false;
 
            statsElig++;

            rand = state.random.nextDouble();

            //if(sim.extendedOutput)System.out.println(avgPart);
            //if(sim.extendedOutput)System.out.println(obsparticipationRounds.get(runningRound));
            //if(rand < avgPart)
            if(rand < obsparticipationRounds.get(runningRound))
            {
                humans1Dose.add(h);

                rand = state.random.nextDouble();

                wasTreated = true;

                statsTreat1++;

                statsEffTreatNorm++;

                //if(rand < sim.treat1Eff)
                if(rand < sim.niclosamideTreatEff)
                {
                    statsEffTreat++;

                    //System.out.println("Human first treatment");
                    h.treat();
                }
            }

            rand = state.random.nextDouble();

            if(wasTreated && rand > avgProbTreatNoTreat)
            {
                if(!humans1Dose.contains(h))humans2Doses.add(h);
                else humans1Dose.add(h);

                rand = state.random.nextDouble();

                statsTreat2++;

                statsEffTreatNorm++;

                //if(rand < sim.treat2Eff)
                if(rand < sim.niclosamideTreatEff)
                {
                    statsEffTreat++;
                    h.markedForTreat = true;
                    h.treatTimer = 3;
                }
            }

            rand = state.random.nextDouble();

            if(!wasTreated && rand < avgProbNoTreatTreat)
            {
                rand = state.random.nextDouble();

                statsTreat2++;

                //if(rand < sim.treat2Eff)
                if(rand < sim.niclosamideTreatEff)
                {
                    h.markedForTreat = true;
                    h.treatTimer = 3;
                }
            }


        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " Num treated treated first dose: " + statsTreat1);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num treated treated second dose: " + statsTreat2);


        double newValue = numTreated1DoseRing.get(runningRound) + (double)statsTreat1;
        numTreated1DoseRing.set(runningRound, newValue);

        newValue = propTreated1DoseRing.get(runningRound) + (double)statsTreat1/(double)numEligibleParticipantsRing.get(runningRound);
        propTreated1DoseRing.set(runningRound, newValue);

        newValue = numTreated2DoseRing.get(runningRound) + (double)statsTreat2;
        numTreated1DoseRing.set(runningRound, newValue);

        newValue = propTreated2DoseRing.get(runningRound) + (double)statsTreat2/(double)numEligibleParticipantsRing.get(runningRound);
        propTreated2DoseRing.set(runningRound, newValue);

        newValue = propEffTreatments.get(runningRound) + (double)statsEffTreat/(double)statsEffTreatNorm;
        propEffTreatments.set(runningRound, newValue);


    }

    //====================================================
    public void vaccinate(double vaccPart)
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " Vaccination of Pigs week: " + sim.numWeeks);

        for(int i = 1; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(pig.age >= sim.pigAgeEligibleForTreatment)
            {
                double rand = state.random.nextDouble();
 
                if(rand < sim.vaccPart)
                {
                    pig.vaccDose++;
                }
            }


            if(pig.vaccDose == 2)
            {
                pig.vaccDose = 0;

                double rand = state.random.nextDouble();

                if(rand < sim.vaccEff)
                {
                    pig.vaccinated = true;
                    pig.treatmentProtectedTime = 0;
                    pig.treated = true;

                }
            
            }
 

        }
    }



    //====================================================
    public void ringTreatP()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + "----");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Ring treatment of Pigs round: " + runningRound);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Pig Treatment participation: " + pigTreatPartRounds.get(sim.roundSero));
        String time = getTime(intCounter, "interventions");
        if(sim.extendedOutput)System.out.println(sim.villageName + time);

        int statsTreat = 0;
        int statsEligible = 0;
        int statsAge = 0;

        for(int i = 1; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);
            
            if(!pig.eligibleP)continue;

            statsEligible++;

            if(pig.age < sim.pigAgeEligibleForTreatment)
            {
                statsAge++;
                continue;
            }

            pig.eligibleP = false;

            double rand = state.random.nextDouble();
 
            if(rand < pigTreatPartRounds.get(sim.roundSero))
            {
               statsTreat++;
               if(pig.numCysts > 0)
               {
                      pig.treatOXF(); 
               }
            }
        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " Num pigs eligible: " + statsEligible);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num pigs too young to be treated: " + statsAge);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Num pigs treated: " + statsTreat);
    }

    //====================================================
    public void readHumansInterventionsData()
    {
        String inputFile = "";
        String sheetName = "";

        for(int i = 0; i < (sim.intRounds + 2); i++)
        {
            statsPart.add(0);
            statsNoPart.add(0);

            stats2TreatNoTreat.add(0);
            stats2NoTreatTreat.add(0);

            obsparticipationRounds.add(0.0);
            obsparticipationRounds2TreatNoTreat.add(0.0);
            obsparticipationRounds2NoTreatTreat.add(0.0);

            obsparticipationRoundsRaw.add(0.0);
            obsparticipationRounds2TreatNoTreatRaw.add(0.0);
            obsparticipationRounds2NoTreatTreatRaw.add(0.0);
        }

        if(sim.village.interventionType.equals("Ring Trt")
                ||  sim.village.interventionType.equals("Ring Trt (P)")
          )
        {
            inputFile = "./inputData/interventions/R01/R01OA_Data_AnilloTrat.xlsx";
            sheetName = "DataAnilloTrata";
        }
        else if(sim.village.interventionType.equals("Ring Scr")
                ||  sim.village.interventionType.equals("Ring Scr (P)")
          )
        {
            inputFile = "./inputData/interventions/R01/R01OA_Data_AnilloDetecc.xlsx";
            sheetName = "DataAnilloDetecc";
        }
        else if(sim.village.interventionType.equals("Mass Trt")
                ||  sim.village.interventionType.equals("Mass Trt (P)")
               )
        {
            inputFile = "./inputData/interventions/R01/R01OA_Data_BrazoMasivo_300118.xlsx";
            sheetName = "data_grl";
        }

        if(sim.extendedOutput)System.out.println ("Humans intervention input file: " + inputFile);

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
                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");  
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
                    if(sim.village.interventionType.equals("Ring Trt")
                            ||  sim.village.interventionType.equals("Ring Trt (P)")
                      )
                    {
                        processLineRT(line);
                        statsLineProcessed++;
                    }
                    else if(sim.village.interventionType.equals("Ring Scr")
                            ||  sim.village.interventionType.equals("Ring Scr (P)")
                           )
                    {
                        processLineRS(line);
                        statsLineProcessed++;
                    }
                    else if(sim.village.interventionType.equals("Mass Trt")
                            ||  sim.village.interventionType.equals("Mass Trt (P)")
                           )
                    {
                        if(line.get(0).equals(""))continue;
                        processLineMT(line);
                        statsLineProcessed++;
                    }
                }
                else if(sim.extendedOutput)System.out.println(sim.villageName + " line not processed");

            }

            if(sim.extendedOutput)System.out.println(sim.villageName + "num line processed in the human interventions file: " + statsRows);

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

        oldFinalIntTimer = (int)Math.round((double)weeksDiffFinalRound/(double)statsWeeksDiffFinalRound);
        finalIntTimer = Integer.valueOf(oldFinalIntTimer);

        if(sim.extendedOutput)System.out.println(finalIntTimer + " " + oldFinalIntTimer);

        for(int roundRead = 1; roundRead < (sim.intRounds + 2); roundRead++)
        {
            if((statsPart.get(roundRead) + statsNoPart.get(roundRead)) != 0.0)
            obsparticipationRounds.set(roundRead, (double)statsPart.get(roundRead)
                    /(double)(statsPart.get(roundRead) + statsNoPart.get(roundRead)));
            else obsparticipationRounds.set(roundRead, 0.0);

            if((statsPart.get(roundRead)) != 0.0)
            obsparticipationRounds2TreatNoTreat.set(roundRead, (double)stats2TreatNoTreat.get(roundRead)
                    /(double)(statsPart.get(roundRead)));
            else obsparticipationRounds2TreatNoTreat.set(roundRead, 0.0);

            if((statsNoPart.get(roundRead)) != 0.0)
            obsparticipationRounds2NoTreatTreat.set(roundRead, (double)stats2NoTreatTreat.get(roundRead)
                    /(double)(statsNoPart.get(roundRead)));
            else obsparticipationRounds2NoTreatTreat.set(roundRead, 0.0);

            obsnumTreated1DoseRing.set(roundRead, ((double)stats2TreatNoTreat.get(roundRead) + (double)stats2NoTreatTreat.get(roundRead)));
            obsnumTreated2DoseRing.set(roundRead, ((double)statsPart.get(roundRead) + (double)stats2TreatNoTreat.get(roundRead)));

            obspropTreated1DoseRing.set(roundRead, ((double)stats2TreatNoTreat.get(roundRead) + (double)stats2NoTreatTreat.get(roundRead))/(double)statsPart.get(roundRead));
            obspropTreated2DoseRing.set(roundRead, ((double)statsPart.get(roundRead) + (double)stats2TreatNoTreat.get(roundRead))/(double)statsPart.get(roundRead));

            //System.out.println(statsPart.get(roundRead));
        }

        obsPrevHTFinal = obsPrevHTFinal/obsPrevHTFinalNorm;

        if(sim.extendedOutput)System.out.println("----------------------");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Lines processed: " + statsLineProcessed);
        if(sim.extendedOutput)System.out.println(sim.villageName + " Average interval betwen last round and final round: " + oldFinalIntTimer);
        if(sim.extendedOutput)System.out.println("");

        int normPart = 0;

        for(int roundRead = 1; roundRead < (sim.intRounds + 2); roundRead++)
        {
            if(obsparticipationRounds.get(roundRead) != 0.0)
            {
                avgPart = avgPart + obsparticipationRounds.get(roundRead); 
                avgProbTreatNoTreat = avgProbTreatNoTreat + obsparticipationRounds2TreatNoTreat.get(roundRead); 
                avgProbNoTreatTreat = avgProbNoTreatTreat + obsparticipationRounds2NoTreatTreat.get(roundRead); 

                normPart++;
            }

            obsparticipationRoundsRaw.set(roundRead, obsparticipationRounds.get(roundRead));
            obsparticipationRounds2TreatNoTreatRaw.set(roundRead, obsparticipationRounds2TreatNoTreat.get(roundRead));
            obsparticipationRounds2NoTreatTreatRaw.set(roundRead, obsparticipationRounds2NoTreatTreat.get(roundRead));

        }

        //print transition between no treat and treat from the first to the second dose
        if(normPart != 0.0)
        {
            avgPart = avgPart/normPart;
            avgProbTreatNoTreat = avgProbTreatNoTreat/normPart;
            avgProbNoTreatTreat = avgProbNoTreatTreat/normPart;
        }
        else
        {
            avgPart = 0.0;
            avgProbTreatNoTreat = 0.0;
            avgProbNoTreatTreat = 0.0;
        }

        if(sim.extendedOutput)System.out.println("");
        if(sim.extendedOutput)System.out.println("Average Participation first dose: " + avgPart);
        if(sim.extendedOutput)System.out.println("Average Avg. Prob. Treated No Treated: " + avgProbTreatNoTreat);
        if(sim.extendedOutput)System.out.println("Average Avg. Prob. No Treated Treated: " + avgProbNoTreatTreat);

        for(int roundRead = 1; roundRead < (sim.intRounds + 2); roundRead++)
        {
            if(obsparticipationRounds.get(roundRead) == 0.0)
            {
                obsparticipationRounds.set(roundRead, avgPart); 
                obsparticipationRounds2TreatNoTreat.set(roundRead, avgProbTreatNoTreat);
                obsparticipationRounds2NoTreatTreat.set(roundRead, avgProbNoTreatTreat);
            }
        }


        for(int roundRead = 1; roundRead < (sim.intRounds + 2); roundRead++)
        {
            //if(sim.village.interventionType.equals("Mass Trt")
            //        || sim.village.interventionType.equals("Mass Trt (P)")
            //        || sim.village.interventionType.equals("Ring Trt")
            //        || sim.village.interventionType.equals("Ring Trt (P)"))
            //{
            if(sim.extendedOutput)System.out.println(" ");
            if(sim.extendedOutput)System.out.println("---- Participation round " + roundRead);

            if(sim.extendedOutput)System.out.println("---- first dose ----");
            if(sim.extendedOutput)System.out.println("Participation first dose: " + obsparticipationRounds.get(roundRead));
            if(sim.extendedOutput)System.out.println("Num individuals that received a niclosamide dose round " + roundRead + ": " + statsPart.get(roundRead));
            if(sim.extendedOutput)System.out.println("Num individuals that not received a niclosamide dose round " + roundRead + ": " + statsNoPart.get(roundRead));
            //}


            if(sim.village.interventionType.equals("Ring Trt")
                    ||  sim.village.interventionType.equals("Ring Trt (P)"))
            {
                if(sim.extendedOutput)System.out.println("---- second dose ----");
                if(sim.extendedOutput)System.out.println("Prob of 1st dose treated 2nd dose not treated: " + obsparticipationRounds2TreatNoTreat.get(roundRead));
                if(sim.extendedOutput)System.out.println("Prob of 1st dose not treated 2nd dose treated: " + obsparticipationRounds2NoTreatTreat.get(roundRead));
            }

            if(sim.village.interventionType.equals("Ring Scr")
                    ||  sim.village.interventionType.equals("Ring Scr (P)"))
            {
                if(sim.extendedOutput)System.out.println("---- Stool screening ----");
                if(sim.extendedOutput)System.out.println("Stool screening participation: " + obsparticipationRounds.get(roundRead));
                if(sim.extendedOutput)System.out.println("Num individuals that had stool screened: " + statsPart.get(roundRead));
                if(sim.extendedOutput)System.out.println("Num individuals that not had stool screened: " + statsNoPart.get(roundRead));

            }

        }

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println("---- Final mass treatment round");
        if(sim.extendedOutput)System.out.println("Participation final mass treat: " + obsparticipationRounds.get(sim.intRounds + 1));
        if(sim.extendedOutput)System.out.println("Final round human taeniasis prevalence: " + obsPrevHTFinal);

        //System.exit(0);
    }

    //====================================================
    public void processLineMT(List<String> line)
    {
        //if(sim.extendedOutput)System.out.println(line);

        String vill = "";
        String tmp  = "";

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");  
        Calendar cal5 = Calendar.getInstance();
        Calendar cal6 = Calendar.getInstance();

        cal5 = null;
        cal6 = null;

        for(int roundRead = 1; roundRead < (sim.intRounds + 2); roundRead++)
        {
            if(roundRead == 1)
            {
                vill = line.get(2);
                if(!vill.equals(sim.villageNameNumber))continue;
                tmp = line.get(17); 
            }
            else if(roundRead == 2)
            {
                vill = line.get(39);
                if(!vill.equals(sim.villageNameNumber))continue;
                tmp = line.get(54); 
            }
            else if(roundRead == 3)
            {
                vill = line.get(76);
                if(!vill.equals(sim.villageNameNumber))continue;
                tmp = line.get(91); 
            }
            else if(roundRead == 4)
            {
                vill = line.get(113);
                if(!vill.equals(sim.villageNameNumber))continue;
                tmp = line.get(128); 
            }
            else if(roundRead == 5)
            {
                vill = line.get(150);
                if(!vill.equals(sim.villageNameNumber))continue;
                tmp = line.get(165); 
                cal5 = null;
                if(!tmp.equals(".") && !tmp.equals(""))
                {
                    //System.out.println("first date: " + tmp);
                    cal5 = Calendar.getInstance();
                    try
                    {
                        cal5.setTime(dateFormat.parse(tmp));
                    }
                    catch (ParseException e)
                    {
                        System.out.println(e);
                        System.exit(0);
                    }
                }
            }
            else if(roundRead == 6)
            {
                vill = line.get(187);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(231); 
                cal6 = null;
                if(!tmp.equals(".") && !tmp.equals(""))
                {
                    //System.out.println("second date: " + tmp);
                    cal6 = Calendar.getInstance();
                    try
                    {
                        cal6.setTime(dateFormat.parse(tmp));
                    }
                    catch (ParseException e)
                    {
                        System.out.println(e);
                        System.exit(0);
                    }
                }

                if(line.get(268).equals("POSITIVO")
                        || line.get(268).equals("POSITIVO2")
                  )obsPrevHTFinal++;

                if(!line.get(255).equals(".")
                  )obsPrevHTFinalNorm++;

            }

            if(tmp.equals("."))statsNoPart.set(roundRead, (statsNoPart.get(roundRead) + 1));
            else statsPart.set(roundRead, (statsPart.get(roundRead) + 1));


        }

        if(cal5 != null && cal6 != null)
        {
            int weekDiff = cal6.get(Calendar.WEEK_OF_YEAR) - cal5.get(Calendar.WEEK_OF_YEAR);
            weeksDiffFinalRound = weeksDiffFinalRound + weekDiff;
            statsWeeksDiffFinalRound++;

            //System.out.println("---------------");
            //System.out.println(weekDiff);
            //System.out.println(cal5.get(Calendar.WEEK_OF_YEAR));
            //System.out.println(cal6.get(Calendar.WEEK_OF_YEAR));
        }
        else
        {
            //putative interval between last round a final treatment if no people was
            //treated in round 7
            int weekDiff = (int)Math.round(sim.weeksInAMonth * 1.6);
            weeksDiffFinalRound = weeksDiffFinalRound + weekDiff;
            statsWeeksDiffFinalRound++;
        }




    }

    //====================================================
    public void processLineRT(List<String> line)
    {
        //if(sim.extendedOutput)System.out.println(line);


        String vill = "";
        String tmp = "";
        String tmp2 = "";

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");  
        Calendar cal7 = Calendar.getInstance();
        Calendar cal8 = Calendar.getInstance();

        cal7 = null;
        cal8 = null;

        for(int roundRead = 1; roundRead < (sim.intRounds + 2); roundRead++)
        {
            tmp = null;
            tmp2 = null;
            vill = "";

            if(roundRead == 1)
            {
                vill = line.get(7);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(18); 
                tmp2 = line.get(40); 
            }
            else if(roundRead == 2)
            {
                vill = line.get(62);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(73); 
                tmp2 = line.get(95); 
            }
            else if(roundRead == 3)
            {
                vill = line.get(117);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(128); 
                tmp2 = line.get(150); 
            }
            else if(roundRead == 4)
            {
                vill = line.get(172);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(183); 
                tmp2 = line.get(205); 
            }
            else if(roundRead == 5)
            {
                vill = line.get(227);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(238); 
                tmp2 = line.get(260); 
            }
            else if(roundRead == 6)
            {
                vill = line.get(282);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(293); 
                tmp2 = line.get(315); 
            }
            else if(roundRead == 7)
            {
                vill = line.get(337);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(348); 
                tmp2 = line.get(370); 

                cal7 = null;
                if(!tmp.equals(".") && !tmp.equals(""))
                {
                    //System.out.println("first date: " + tmp);
                    cal7 = Calendar.getInstance();
                    try
                    {
                        cal7.setTime(dateFormat.parse(tmp));
                    }
                    catch (ParseException e)
                    {
                        System.out.println(e);
                        System.exit(0);
                    }
                }
            }
            else if(roundRead == 8)
            {
                vill = line.get(391);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(438); 
                tmp2 = ".";
                cal8 = null;
                if(!tmp.equals(".") && !tmp.equals(""))
                {
                    //System.out.println("second date: " + tmp);
                    cal8 = Calendar.getInstance();
                    try
                    {
                        cal8.setTime(dateFormat.parse(tmp));
                    }
                    catch (ParseException e)
                    {
                        System.out.println(e);
                        System.exit(0);
                    }
                }

                //resultado column = 1
                if(line.get(475).equals("1")
                  )obsPrevHTFinal++;

                if(!line.get(462).equals(".")
                  )obsPrevHTFinalNorm++;
            }

            if(tmp.equals("."))statsNoPart.set(roundRead, (statsNoPart.get(roundRead) + 1));
            else statsPart.set(roundRead, (statsPart.get(roundRead) + 1));

            if(tmp.equals(".") && !tmp2.equals("."))stats2NoTreatTreat.set(roundRead, (stats2NoTreatTreat.get(roundRead) + 1));

            if(!tmp.equals(".") && tmp2.equals("."))stats2TreatNoTreat.set(roundRead, (stats2TreatNoTreat.get(roundRead) + 1));


        }

        if(cal7 != null && cal8 != null)
        {
            int weekDiff = cal8.get(Calendar.WEEK_OF_YEAR) - cal7.get(Calendar.WEEK_OF_YEAR);
            weeksDiffFinalRound = weeksDiffFinalRound + weekDiff;
            statsWeeksDiffFinalRound++;

            //System.out.println("---------------");
            //System.out.println(weekDiff);
            //System.out.println(cal7.get(Calendar.WEEK_OF_YEAR));
            //System.out.println(cal8.get(Calendar.WEEK_OF_YEAR));
        }
        else
        {
            //putative interval between last round a final treatment if no people was
            //treated in round 7
            int weekDiff = (int)Math.round(sim.weeksInAMonth * 1.6);
            weeksDiffFinalRound = weeksDiffFinalRound + weekDiff;
            statsWeeksDiffFinalRound++;
        }



    }

    //====================================================
    public void processLineRS(List<String> line)
    {
        //if(sim.extendedOutput)System.out.println(line);


        String vill = "";
        String tmp = "";

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");  
        Calendar cal7 = Calendar.getInstance();
        Calendar cal8 = Calendar.getInstance();

        cal8 = null;
        cal7 = null;

        for(int roundRead = 1; roundRead < (sim.intRounds + 2); roundRead++)
        {
            tmp = null;
            vill = "";

            if(roundRead == 1)
            {
                vill = line.get(11);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(29); 
            }
            else if(roundRead == 2)
            {
                vill = line.get(69);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(87); 
            }
            else if(roundRead == 3)
            {
                vill = line.get(127);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(147); 
            }
            else if(roundRead == 4)
            {
                vill = line.get(185);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(203); 
            }
            else if(roundRead == 5)
            {
                vill = line.get(243);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(261); 
            }
            else if(roundRead == 6)
            {
                vill = line.get(301);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(319); 
            }
            else if(roundRead == 7)
            {
                vill = line.get(359);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(377); 

                cal7 = null;
                if(!tmp.equals(".") && !tmp.equals(""))
                {
                    //System.out.println("first date: " + tmp);
                    cal7 = Calendar.getInstance();
                    try
                    {
                        cal7.setTime(dateFormat.parse(tmp));
                    }
                    catch (ParseException e)
                    {
                        System.out.println(e);
                        System.exit(0);
                    }
                }
            }
            else if(roundRead == 8)
            {
                vill = line.get(413);
                if(!vill.equals(sim.villageNameNumber))continue;

                tmp = line.get(460); 
                cal8 = null;
                if(!tmp.equals(".") && !tmp.equals(""))
                {
                    //System.out.println("second date: " + tmp);
                    cal8 = Calendar.getInstance();
                    try
                    {
                        cal8.setTime(dateFormat.parse(tmp));
                    }
                    catch (ParseException e)
                    {
                        System.out.println(e);
                        System.exit(0);
                    }
                }

                if(line.get(498).equals("1")
                  )obsPrevHTFinal++;

                if(!line.get(484).equals(".")
                  )obsPrevHTFinalNorm++;
            }


            if(tmp.equals("."))statsNoPart.set(roundRead, (statsNoPart.get(roundRead) + 1));
            else statsPart.set(roundRead, (statsPart.get(roundRead) + 1));
        }

        if(cal7 != null && cal8 != null)
        {
            int weekDiff = cal8.get(Calendar.WEEK_OF_YEAR) - cal7.get(Calendar.WEEK_OF_YEAR);
            weeksDiffFinalRound = weeksDiffFinalRound + weekDiff;
            statsWeeksDiffFinalRound++;

            //System.out.println("---- weeks diff ---------------");
            //System.out.println(weekDiff);
            //System.out.println(cal7.get(Calendar.WEEK_OF_YEAR));
            //System.out.println(cal8.get(Calendar.WEEK_OF_YEAR));
        }
        else
        {
            //putative interval between last round a final treatment if no people was
            //treated in round 7
            int weekDiff = (int)Math.round(sim.weeksInAMonth * 1.6);
            weeksDiffFinalRound = weeksDiffFinalRound + weekDiff;
            statsWeeksDiffFinalRound++;
        }


    }

    //====================================================
    public void readPigParticipation()
    {
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/interventions/R01/R01_PigParticipation.xlsx";
        //if(sim.extendedOutput)System.out.println(sim.village.interventionType);
        if(sim.village.interventionType.equals("Mass Trt (P)"))
        {
            sheetName = "Mass Trt (P)";
        }
        else if(sim.village.interventionType.equals("Ring Trt (P)"))
        {
            sheetName = "Ring Trt (P)";
        }
        else if(sim.village.interventionType.equals("Ring Scr (P)"))
        {
            sheetName = "Ring Scr (P)";
        }
        else return;//no pig treatment was done in the study arm

        if(sim.extendedOutput)System.out.println(" ");
        if(sim.extendedOutput)System.out.println("---- Reading pigs participation data ----");

        if(sim.extendedOutput)System.out.println ("Participation input file: " + inputFile);
        if(sim.extendedOutput)System.out.println ("Sheet of file: " + sheetName);

        int readRound = 1;

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

                    //if(sim.extendedOutput)System.out.println ("cellType : " + cell.getCellType());

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            double d = (double)cell.getNumericCellValue();
                            //int aaa = (int)Math.round(d);
                            stri = Double.toString(d);
                            break;
                        default:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                    }
                    line.add(stri);
                    //if(sim.extendedOutput)System.out.println (line);
                }

                //if(sim.extendedOutput)System.out.println (line);

                String delims = "\\.";
                String[] words = line.get(0).split(delims);

                //if(sim.extendedOutput)System.out.println ("-" + words[0] + "-" + " -" + sim.villageNameNumber + "-");
                //if(sim.extendedOutput)System.out.println (line);

                if(!sim.villageNameNumber.equals(words[0]))continue;

                //if(sim.extendedOutput)System.out.println (sim.village.interventionType);

                //if(sim.village.interventionType.equals("Mass Trt (P)"))
                //{
                //if(sim.extendedOutput)System.out.println (line);
                Double norm = Double.parseDouble(line.get(2)); 
                if(norm > 0.0)pigTreatPartRounds.set(readRound, Double.parseDouble(line.get(3))/norm);
                else pigTreatPartRounds.set(readRound, 0.0);
                //if(sim.extendedOutput)System.out.println ("Pigs treatment participation round " + readRound  + ": " + pigTreatPartRounds.get(readRound));

                readRound++;

                //}
                //else if(sim.village.interventionType.equals("Ring Trt (P)") ||
                //        sim.village.interventionType.equals("Ring Scr (P)"))
                //{
                //    sim.treatPartP = Double.parseDouble(line.get(1));
                //    if(sim.extendedOutput)System.out.println ("Pigs treatment participation: " + sim.treatPartP);
                //}


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


        int size = pigTreatPartRounds.size();
        int stats = 0;
        double avg = 0.0;

        for(int i = 0; i < size; i++)
        {
            Double part = pigTreatPartRounds.get(i);
            if(part != 0.0)
            {
                avg = avg + part;
                stats++;
            }
        }

        if(stats != 0)avg = avg/(double)stats;


        for(int i = 0; i < size; i++)
        {
            Double part = pigTreatPartRounds.get(i);
            if(part == 0.0)
            {
                pigTreatPartRounds.set(i, avg);

            }
        }


        for(int i = 0; i < size; i++)
        {
            Double part = pigTreatPartRounds.get(i);

            if(sim.extendedOutput)System.out.println ("Round: " + i + " Pigs treatment participation: " + part);
        }

        //System.exit(0);


    }





    //====================================================
    public void readBaselineFiles()
    {
        if(sim.extendedOutput)System.out.println("---- Reading baseline interventions files");
        String inputFile = "";
        String sheetName = "";

        inputFile = "./inputData/interventions/R01_Baseline_FINAL.xlsx";
        sheetName = "R01_Baseline_FINAL";

        if(sim.extendedOutput)System.out.println ("Interventions input file: " + inputFile);

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
                            double d = (double)cell.getNumericCellValue();
                            int aaa = (int)Math.round(d);
                            stri = Integer.toString(aaa);
                            break;
                        default:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                    }
                    line.add(stri);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                List<Double> tmp = new ArrayList<Double>();
                double ll = 0.0;
                double ul = 0.0;
                if(sim.villageDataset.equals("R01"))
                {
                    if(line.get(0).equals(sim.villageNameNumber))
                    {
                        //tn -------------------------
                        tmp = new ArrayList<Double>();
                        tmp.add(Double.parseDouble(line.get(14))); 
                        if(sim.extendedOutput)System.out.println ("Baseline tn: " + line.get(14));

                        tmp.add(Double.parseDouble(line.get(15))); 
                        if(sim.extendedOutput)System.out.println ("Baseline tn ll: " + line.get(15));

                        tmp.add(Double.parseDouble(line.get(16))); 
                        if(sim.extendedOutput)System.out.println ("Baseline tn ul: " + line.get(16));

                        ll = tmp.get(0) - tmp.get(1);
                        ul = tmp.get(0) + tmp.get(2);
                        tmp.add(ll);
                        tmp.add(ul);

                        sim.baselineData.put("tn", tmp);

                        //light pig inf -------------------------
                        tmp = new ArrayList<Double>();
                        ll = 0.0;
                        ul = 0.0;

                        tmp.add(Double.parseDouble(line.get(6))); 
                        if(sim.extendedOutput)System.out.println ("Baseline light: " + line.get(6));

                        tmp.add(Double.parseDouble(line.get(7))); 
                        if(sim.extendedOutput)System.out.println ("Baseline light ll: " + line.get(7));

                        tmp.add(Double.parseDouble(line.get(8))); 
                        if(sim.extendedOutput)System.out.println ("Baseline light ul: " + line.get(8));

                        ll = tmp.get(0) - tmp.get(1);
                        ul = tmp.get(0) + tmp.get(2);
                        tmp.add(ll);
                        tmp.add(ul);

                        sim.baselineData.put("light", tmp);

                        //heavy pig inf -------------------------
                        tmp = new ArrayList<Double>();
                        ll = 0.0;
                        ul = 0.0;

                        tmp.add(Double.parseDouble(line.get(11))); 
                        if(sim.extendedOutput)System.out.println ("Baseline heavy: " + line.get(11));

                        tmp.add(Double.parseDouble(line.get(12))); 
                        if(sim.extendedOutput)System.out.println ("Baseline heavy ll: " + line.get(12));

                        tmp.add(Double.parseDouble(line.get(13))); 
                        if(sim.extendedOutput)System.out.println ("Baseline heavy ul: " + line.get(13));

                        ll = tmp.get(0) - tmp.get(1);
                        ul = tmp.get(0) + tmp.get(2);
                        tmp.add(ll);
                        tmp.add(ul);

                        sim.baselineData.put("heavy", tmp);

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

    //====================================================
    public void readFinalRoundFiles()
    {
        if(sim.extendedOutput)System.out.println("---- Reading finalround interventions files");
        String inputFile = "";
        String sheetName = "";

        if(sim.villageDataset.equals("R01"))
        {
            inputFile = "./inputData/interventions/R01_FinalRound_FINAL.xlsx";
            sheetName = "R01_FinalRound_FINAL";
        }
        if(sim.extendedOutput)System.out.println ("Interventions input file: " + inputFile);

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
                            double d = (double)cell.getNumericCellValue();
                            int aaa = (int)Math.round(d);
                            stri = Integer.toString(aaa);
                            break;
                        default:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                    }
                    line.add(stri);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                List<Double> tmp = new ArrayList<Double>();
                double ll = 0.0;
                double ul = 0.0;
                if(sim.villageDataset.equals("R01"))
                {
                    if(line.get(0).equals(sim.villageNameNumber))
                    {
                        //tn -------------------------
                        tmp = new ArrayList<Double>();
                        tmp.add(Double.parseDouble(line.get(14))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound tn: " + line.get(14));

                        tmp.add(Double.parseDouble(line.get(15))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound tn ll: " + line.get(15));

                        tmp.add(Double.parseDouble(line.get(16))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound tn ul: " + line.get(16));

                        ll = tmp.get(0) - tmp.get(1);
                        ul = tmp.get(0) + tmp.get(2);
                        tmp.add(ll);
                        tmp.add(ul);

                        sim.finalRoundData.put("tn", tmp);

                        //light pig inf -------------------------
                        tmp = new ArrayList<Double>();
                        ll = 0.0;
                        ul = 0.0;

                        tmp.add(Double.parseDouble(line.get(6))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound light: " + line.get(6));

                        tmp.add(Double.parseDouble(line.get(7))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound light ll: " + line.get(7));

                        tmp.add(Double.parseDouble(line.get(8))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound light ul: " + line.get(8));

                        ll = tmp.get(0) - tmp.get(1);
                        ul = tmp.get(0) + tmp.get(2);
                        tmp.add(ll);
                        tmp.add(ul);

                        sim.finalRoundData.put("light", tmp);

                        //heavy pig inf -------------------------
                        tmp = new ArrayList<Double>();
                        ll = 0.0;
                        ul = 0.0;

                        tmp.add(Double.parseDouble(line.get(11))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound heavy: " + line.get(11));

                        tmp.add(Double.parseDouble(line.get(12))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound heavy ll: " + line.get(12));

                        tmp.add(Double.parseDouble(line.get(13))); 
                        if(sim.extendedOutput)System.out.println ("FinalRound heavy ul: " + line.get(13));

                        ll = tmp.get(0) - tmp.get(1);
                        ul = tmp.get(0) + tmp.get(2);
                        tmp.add(ll);
                        tmp.add(ul);

                        sim.finalRoundData.put("heavy", tmp);

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

    //====================================================
    public void readMidRoundFiles()
    {
        if(sim.extendedOutput)System.out.println("---- Reading Midround interventions files");
        String inputFile = "";
        String sheetName = "";

        if(sim.villageDataset.equals("R01"))
        {
            return;
        }
        if(sim.extendedOutput)System.out.println ("Interventions input file: " + inputFile);

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
                            double d = (double)cell.getNumericCellValue();
                            int aaa = (int)Math.round(d);
                            stri = Integer.toString(aaa);
                            break;
                        default:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                    }
                    line.add(stri);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                List<Double> tmp = new ArrayList<Double>();
                double ll = 0.0;
                double ul = 0.0;

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



    //====================================================
    public void tongueInspectRound()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " =========================================");
        if(sim.extendedOutput)System.out.println(sim.villageName + " Tongue Inspection Pigs round: " + runningRound);
        String time = getTime(intCounter, "interventions");
        if(sim.extendedOutput)System.out.println(sim.villageName + time);

        int stats = 0;

        List<Household> hhInRings = new ArrayList<Household>();
        List<Human> eligibleHumans = new ArrayList<Human>();
        List<Household> eligibleHouses = new ArrayList<Household>();

        //double radius = sim.ringSize/sim.geoCellSize;

        //if(sim.extendedOutput)System.out.println(sim.villageName + " geoCell size: " + sim.geoCellSize);

        for(int i = 1; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            double rand = state.random.nextDouble();
            if(rand  >= sim.tonguePart)continue;
                
            Boolean tonguePositive = pig.tonguePalpation();
            if(!tonguePositive)continue;

            stats++;

            if(sim.extendedOutput)System.out.println(sim.villageName  + " -----");
            if(sim.extendedOutput)System.out.println(sim.villageName + " Pig identity num: " + pig.identity + " household: " + pig.household.shpId + " positive to tongue inspection");
            //if(sim.extendedOutput)System.out.println(sim.villageName + " Pig house shpId: " + pig.household.shpId);
            //if(sim.extendedOutput)System.out.println(sim.villageName + " Pig house coords: " + pig.household.cpPosition.xcor + " " + pig.household.cpPosition.ycor);
            //if(sim.extendedOutput)System.out.println(sim.villageName + " Pig coords: " + pig.cpPosition.xcor + " " + pig.cpPosition.ycor);

            if(!hhInRings.contains(pig.household))
            {
                hhInRings.add(pig.household);
            }
                

            //humans in range
            Bag bag = getHumansInRangePig(pig);

            for(int h = 0; h < bag.size(); h++)
            {
                Human hh = (Human)bag.get(h);
                //if(hh.strangerTraveler)System.out.println("---- Human strangerTraveler inside Ring");
                hh.eligibleH = true;

                if(!eligibleHumans.contains(hh))eligibleHumans.add(hh);
                if(!eligibleHouses.contains(hh.household))eligibleHouses.add(hh.household);
            }

            //pigs in range
            bag = getPigsInRangePig(pig);

            for(int p = 0; p < bag.size(); p++)
            {
                Pig pp = (Pig)bag.get(p);
                pp.eligibleP = true;
            }

            //tongue positive pig is eliminated from the simulation
            pig.die();
        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + " A total of " + stats + " tongue positive pige were found");

        //store the prev of tongue positive pigs in this round
        double newValue = sim.avgInterPrevTonguePositiveRounds.get(runningRound) + (double)stats/(double)sim.pigsBag.size();
        //if(sim.extendedOutput)System.out.println(newValue);
        sim.avgInterPrevTonguePositiveRounds.set(runningRound, newValue);

        numPigsTonguePositive.set(runningRound, (double)stats);
        propnumPigsTonguePositive.set(runningRound, newValue);

        newValue = (double)hhInRings.size() + numRings.get(runningRound);
        numRings.set(runningRound, newValue);

        newValue = (double)numEligibleParticipantsRing.get(runningRound) + eligibleHumans.size();
        numEligibleParticipantsRing.set(runningRound, newValue);

        newValue = (double)numEligibleParticipantsRing.get(runningRound) + (double)eligibleHumans.size()/(double)sim.humansBag.size();
        propnumEligibleParticipantsRing.set(runningRound, newValue);

        newValue = (double)numEligibleHousesRing.get(runningRound) + eligibleHouses.size();
        numEligibleHousesRing.set(runningRound, newValue);

        newValue = (double)propnumEligibleHousesRing.get(runningRound) + (double)eligibleHouses.size()/(double)sim.householdsBag.size();
        propnumEligibleHousesRing.set(runningRound, newValue);
    }

    //====================================================
    public String getTime(int timer, String what)
    {
        String time = " Time from the beginning of " + what + " ";

        time = time + timer + " weeks ";

        double months = (double)timer/(double)(sim.weeksInAMonth);
        months = months * 100;
        months = Math.round(months);
        months = months / 100;

        time = time + months + " months ";

        return time;

    }


    //====================================================
    public void eraseInfections()
    {
        //if(sim.extendedOutput)System.out.println(sim.villageName + " -----------------------------------------");
        //if(sim.extendedOutput)System.out.println(sim.villageName + " Erasing sim infections");

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            pig.numCysts = 0;
            //pig.seropositive = false;
        }


        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human human = (Human)sim.humansBag.get(i);

            //if(human.strangerTraveler)if(sim.extendedOutput)System.out.println(sim.villageName + "stranger traveler")stranger traveler");

            human.matureTn(false);

            //human.defecationSite.eggsAlive = false;
            //human.defecationSite.eggs = false;
            //human.defecationSite.proglottid = false;

        }
        //if(sim.extendedOutput)System.out.println(sim.villageName + " -----------------------------------------");


    }

    //====================================================
    public void printResume()
    {
        if(!sim.extendedOutput)return;

        String piglets = "";
        String pigs = "";
        String pigsCohorts = "";
        String seroInc = "";

        String pigletsObs = "";
        String pigsObs = "";
        String seroIncObs = "";

        String nRoundSero = "";
        String nRoundTreat = "";

        String participation = "";

        String tongue = "";
        String tongueObs = "";

        String numRingsSim = "";

        String pigsPart = "";

        String humansPart = "";
        String humansPartRaw = "";

        String treatNoTreat = "";
        String noTreatTreat = "";

        String treatNoTreatRaw = "";
        String noTreatTreatRaw = "";

        String obs1Dose = "";
        String obs2Dose = "";

        String obsprop1Dose = "";
        String obsprop2Dose = "";

        double tmp = 0.0;
        int tmpI = 0;

        //-----------------------
        String perPiglets = "";
        if(sim.perseroPrevalencePigletsNorm != 0)tmp = sim.perseroPrevalencePiglets/sim.perseroPrevalencePigletsNorm;
        else sim.perseroPrevalencePiglets = 0.0;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        perPiglets = perPiglets + tmp + " ";

        //-----------------------
        String perYoung = "";
        if(sim.perseroPrevalencePigsYoungNorm != 0)tmp = sim.perseroPrevalencePigsYoung/sim.perseroPrevalencePigsYoungNorm;
        else sim.perseroPrevalencePigsYoung = 0.0;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        perYoung = perYoung + tmp + " ";

        //-----------------------
        String perAdult = "";
        if(sim.perseroPrevalencePigsAdultNorm != 0)tmp = sim.perseroPrevalencePigsAdult/sim.perseroPrevalencePigsAdultNorm;
        else sim.perseroPrevalencePigsAdult = 0.0;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        perAdult = perAdult + tmp + " ";

        //-----------------------
        String perTransition = "";
        if(sim.perseroPrevalenceAgeTransitionNorm != 0)tmp = sim.perseroPrevalenceAgeTransition/sim.perseroPrevalenceAgeTransitionNorm;
        else sim.perseroPrevalenceAgeTransition = 0.0;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        perTransition = perTransition + tmp + " ";

        //-----------------------
        String partFinal = "";
        //tmp = obsparticipationRounds.get(sim.intRounds + 1);
        tmp = 0.0;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        partFinal = partFinal + tmp + " ";

        //-----------------------
        String HTFinal = "";
        tmp = obsPrevHTFinal;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        HTFinal = HTFinal + tmp + " ";
 
        //-----------------------
        String simHTFinal = "";
        tmp = sim.avgSimPrevHTFinal/(double)numInterventionsDone;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        simHTFinal = simHTFinal + tmp + " ";

        //-----------------------
        String simPCFinal = "";
        tmp = sim.avgSimPrevPCFinal/(double)numInterventionsDone;
        tmp = tmp * 100000;
        tmpI = (int)Math.round(tmp);
        tmp = tmpI/(double)100000;
        simPCFinal = simPCFinal + tmp + " ";



        if(sim.village.interventionType.equals("Mass Trt")
                || sim.village.interventionType.equals("Mass Trt (P)")
                || sim.village.interventionType.equals("Ring Trt")
                ||  sim.village.interventionType.equals("Ring Trt (P)")
                ||  sim.village.interventionType.equals("Ring Scr")
                ||  sim.village.interventionType.equals("Ring Scr (P)"))
        {
            for(int round = 1; round <= sim.intRounds; round++)
            {
                tmp = round;
                nRoundTreat = nRoundTreat + tmp + " ";

                //-----------------------
                tmp = obsparticipationRounds.get(round);
                tmp = tmp * 1000;
                tmpI = (int)Math.round(tmp);
                tmp = tmpI/(double)1000;
                humansPart = humansPart + tmp + " ";

                //-----------------------
                tmp = obsparticipationRoundsRaw.get(round);
                tmp = tmp * 1000;
                tmpI = (int)Math.round(tmp);
                tmp = tmpI/(double)1000;
                humansPartRaw = humansPartRaw + tmp + " ";

                if(sim.village.interventionType.equals("Ring Trt")
                        ||  sim.village.interventionType.equals("Ring Trt (P)")
                  )
                {
                    //-----------------------
                    tmp = obsnumTreated1DoseRing.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    obs1Dose = obs1Dose + tmp + " ";

                    //-----------------------
                    tmp = obsnumTreated2DoseRing.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    obs2Dose = obs2Dose + tmp + " ";

                    //-----------------------
                    tmp = obspropTreated1DoseRing.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    obsprop1Dose = obsprop1Dose + tmp + " ";

                    //-----------------------
                    tmp = obspropTreated2DoseRing.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    obsprop2Dose = obsprop2Dose + tmp + " ";
                }
 

                if(sim.village.interventionType.equals("Ring Trt")
                        ||  sim.village.interventionType.equals("Ring Trt (P)")
                        ||  sim.village.interventionType.equals("Ring Scr")
                        ||  sim.village.interventionType.equals("Ring Scr (P)")
                  )
                {
                    //-----------------------
                    tmp = numRings.get(round)/(double)numInterventionsDone;
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    numRingsSim = numRingsSim + tmp + " ";

                    //-----------------------
                    tmp = obsparticipationRounds2TreatNoTreat.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    treatNoTreat = treatNoTreat + tmp + " ";

                    //-----------------------
                    tmp = obsparticipationRounds2NoTreatTreat.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    noTreatTreat = noTreatTreat + tmp + " ";

                    //-----------------------
                    tmp = obsparticipationRounds2TreatNoTreatRaw.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    treatNoTreatRaw = treatNoTreatRaw + tmp + " ";

                    //-----------------------
                    tmp = obsparticipationRounds2NoTreatTreatRaw.get(round);
                    tmp = tmp * 1000;
                    tmpI = (int)Math.round(tmp);
                    tmp = tmpI/(double)1000;
                    noTreatTreatRaw = noTreatTreatRaw + tmp + " ";

                }
 

            }
        }

       
        for(int round = 1; round <= 7; round++)
        {
             tmp = round;
             nRoundSero = nRoundSero + tmp + " ";

             //-----------------------
             tmp = sim.obsPrevTonguePositiveRounds.get(round);
             tmp = tmp * 100000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)100000;
             tongueObs = tongueObs + tmp + " ";

             tmp = sim.avgInterPrevTonguePositiveRounds.get(round)/(double)numInterventionsDone;
             tmp = tmp * 100000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)100000;
             tongue = tongue + tmp + " ";

             //-----------------------
             tmp = sim.obsSeroPrevalencePigletsRounds.get(round);
             tmp = tmp * 1000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)1000;
             pigletsObs = pigletsObs + tmp + " ";

             tmp = sim.avgInterSeroPrevalencePigletsRounds.get(round)/(double)numInterventionsDone;
             tmp = tmp * 1000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)1000;
             piglets = piglets + tmp + " ";

             //-----------------------
             tmp = sim.obsSeroPrevalencePigsRounds.get(round);
             tmp = tmp * 1000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)1000;
             pigsObs = pigsObs + tmp + " ";

             tmp = sim.avgInterSeroPrevalencePigsRounds.get(round)/(double)numInterventionsDone;
             tmp = tmp * 1000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)1000;
             pigs = pigs + tmp + " ";

             //-----------------------
             tmp = sim.avgInterSeroPrevalencePigsRounds.get(round)/(double)numInterventionsDone;
             tmp = tmp * 1000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)1000;
             pigsCohorts = pigsCohorts + tmp + " ";

             //-----------------------
             tmp = sim.obsSeroIncidencePigsRounds.get(round);
             tmp = tmp * 1000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)1000;
             seroIncObs = seroIncObs + tmp + " ";

             //-----------------------
             tmp = sim.avgInterSeroIncidencePigsRounds.get(round)/(double)numInterventionsDone;
             tmp = tmp * 1000;
             tmpI = (int)Math.round(tmp);
             tmp = tmpI/(double)1000;
             seroInc = seroInc + tmp + " ";

             if(sim.village.interventionType.equals("Mass Trt (P)")
                     || sim.village.interventionType.equals("Ring Trt (P)")
                     || sim.village.interventionType.equals("Ring Scr (P)"))
             {
                 //-----------------------
                 tmp = pigTreatPartRounds.get(round);
                 tmp = tmp * 1000;
                 tmpI = (int)Math.round(tmp);
                 tmp = tmpI/(double)1000;
                 pigsPart = pigsPart + tmp + " ";
             }
        }


        //Start printing stats -------------------------------------------------------
        System.out.println(sim.villageName + " ");
        System.out.println(sim.villageName + " -----------------------------------------");
        System.out.println(sim.villageName + " .... Printing stat for the " + sim.village.interventionType + " interventions results");
        System.out.println(sim.villageName + " .... Num of simulated intervention trials: " +  numInterventionsDone);


        if(sim.village.interventionType.equals("Mass Trt")
                || sim.village.interventionType.equals("Mass Trt (P)")
                || sim.village.interventionType.equals("Ring Trt")
                ||  sim.village.interventionType.equals("Ring Trt (P)")
                ||  sim.village.interventionType.equals("Ring Scr")
                ||  sim.village.interventionType.equals("Ring Scr (P)"))
        {
            System.out.println(" ");
            System.out.println(sim.villageName + " Round human treatment:");
            System.out.println(nRoundTreat);


            if(sim.village.interventionType.equals("Ring Scr")
                    ||  sim.village.interventionType.equals("Ring Scr (P)")
                    ||sim.village.interventionType.equals("Ring Trt")
                    ||  sim.village.interventionType.equals("Ring Trt (P)")
              )
            {
                //pitxi

            }

            if(sim.village.interventionType.equals("Ring Scr")
                    ||  sim.village.interventionType.equals("Ring Scr (P)")
              )
            {
                System.out.println(" ");
                System.out.println(sim.villageName + " Humans participation to screenings (raw data)");
                System.out.println(humansPartRaw);
                System.out.println(sim.villageName + " Humans participation to screenings (corrected data)");
                System.out.println(humansPart);
            }
            else
            {
                System.out.println(" ");
                System.out.println(sim.villageName + " Humans participation to treatments (raw data)");
                System.out.println(humansPartRaw);
                System.out.println(sim.villageName + " Humans participation to treatments (corrected data)");
                System.out.println(humansPart);
            }

            if(sim.village.interventionType.equals("Ring Trt")
                    ||  sim.village.interventionType.equals("Ring Trt (P)")
              )
            {
                System.out.println(" ");
                System.out.println(sim.villageName + " Prop of humans that received first dose but not the second (raw data)");
                System.out.println(treatNoTreatRaw);
                System.out.println(sim.villageName + " Prop of humans that received first dose but not the second (corrected data)");
                System.out.println(treatNoTreat);
                System.out.println(sim.villageName + " Prop of humans that received second dose but not the first (raw data)");
                System.out.println(noTreatTreatRaw);
                System.out.println(sim.villageName + " Prop of humans that received second dose but not the first (corrected data)");
                System.out.println(noTreatTreat);

                System.out.println(" ");
                System.out.println(sim.villageName + " Humans that received 1 dose:");
                System.out.println(obs1Dose);
                System.out.println(sim.villageName + " Proportion of Humans that received 1 dose:");
                System.out.println(obsprop1Dose);
                System.out.println(sim.villageName + " Humans that received 2 dose:");
                System.out.println(obs2Dose);
                System.out.println(sim.villageName + " Proportion of Humans that received 2 dose:");
                System.out.println(obsprop2Dose);

            }

        }

        System.out.println(" ");
        System.out.println(sim.villageName + " Participation to the final human mass treatment:");
        System.out.println(partFinal);
        System.out.println(sim.villageName + " Final human observed taeniasis prevalence: ");
        System.out.println(HTFinal);
        System.out.println(sim.villageName + " Final human simulated taeniasis prevalence: ");
        System.out.println(simHTFinal);
        System.out.println(sim.villageName + " Final pig cysticercosis prevalence: ");
        System.out.println(simPCFinal);


        System.out.println(" ");
        System.out.println(sim.villageName + " Rounds serology (same as pigs treatment rounds):");
        System.out.println(nRoundSero);

        if(sim.village.interventionType.equals("Mass Trt (P)")
                || sim.village.interventionType.equals("Ring Trt (P)")
                || sim.village.interventionType.equals("Ring Scr (P)"))
        {

            System.out.println(" ");
            System.out.println(sim.villageName + " Pigs participation to treatments");
            System.out.println(pigsPart);
        }

        System.out.println(" ");
        System.out.println(sim.villageName + " Observed prevalence tongue positive tests: ");
        System.out.println(tongueObs);
        System.out.println(sim.villageName + " Avg. sim prevalence tongue positive tests: ");
        System.out.println(tongue);

        System.out.println(" ");
        System.out.println("Observed serology data vs simulated serology data ");
        System.out.println(sim.villageName + " Observed Piglets seroprevalence: ");
        System.out.println(pigletsObs);
        System.out.println(sim.villageName + " Avg. sim Piglets seroprevalence: ");
        System.out.println(piglets);
        System.out.println(" ");
        System.out.println(sim.villageName + " Observed Pigs seroprevalence: ");
        System.out.println(pigsObs);
        System.out.println(sim.villageName + " Avg. sim Pigs seroprevalence: ");
        System.out.println(pigs);
        System.out.println(sim.villageName + " Avg. sim Pigs Cohorts seroprevalence: ");
        System.out.println(pigsCohorts);
        System.out.println(" ");
        System.out.println(sim.villageName + " Observed Seroincidence: ");
        System.out.println(seroIncObs);
        System.out.println(sim.villageName + " Avg. sim Seroincidence: ");
        System.out.println(seroInc);

        System.out.println(" ");
        System.out.println(sim.villageName + " perSeroprevalence piglets round 1: " + perPiglets);
        System.out.println(sim.villageName + " perSeroprevalence young pigs round 1: " + perYoung);
        System.out.println(sim.villageName + " perSeroprevalence adlut pigs round 1: " + perAdult);
        System.out.println(sim.villageName + " perSeroprevalence transition age round 1: " + perTransition);

        System.out.println(" ");
        System.out.println(sim.villageName + " -----------------------------------------");
        System.out.println(" ");
    }

    //====================================================
    public void writeStatsToFile()
    {

        String fileName = sim.outDir + sim.villageName + "_interventionsStats.xls";

        try {

            FileOutputStream out = 
                new FileOutputStream(new File(fileName));
            workbookOutStats.write(out);
            out.close();
            if(sim.extendedOutput)System.out.println(sim.villageName  + " output spreadsheet of intervention stats written sucessfully.");
            if(sim.extendedOutput)System.out.println("Out file: " + fileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    //====================================================
    public void writeObservedIntersRingStats()
    {
        //erase_file();

        //if(sim.extendedOutput)System.out.println("================================================");
        //if(sim.extendedOutput)System.out.println ("Writing  outputs ..............................");
        //if(sim.extendedOutput)System.out.println(" ");

        HSSFSheet sheet;

        sheet = workbookOutStats.getSheet(statsSheet);
        //If the sheet !exists a new sheet is created --------- 
        if(sheet == null)sheet = workbookOutStats.createSheet(statsSheet);

        workbookOutStats.setSheetOrder(statsSheet, 0);

        Cell cell = null;

        int lastRow = sheet.getLastRowNum();

        //if(sim.extendedOutput)System.out.println("Last row:" + lastRow);
        //lastRow++;

        Row row = sheet.createRow(lastRow++);

        int cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Village");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)sim.villageName);

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Intervention Arm");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)sim.village.interventionType);

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Num humans");

        cell = row.createCell(cellnum++);
        cell.setCellValue((Integer)sim.humansBag.size());

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Num pigs");

        cell = row.createCell(cellnum++);
        cell.setCellValue((Integer)sim.pigsBag.size());

        //-------------------------------------------
        row = sheet.createRow(lastRow++);
        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Num intervention rounds");

        cell = row.createCell(cellnum++);
        cell.setCellValue((Integer)sim.intRounds);

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"weeks between intervention rounds");

        cell = row.createCell(cellnum++);
        cell.setCellValue((Integer)intTime);

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"intervention trial repetitions");

        cell = row.createCell(cellnum++);
        cell.setCellValue((Integer)sim.interventionRepetitions);


        //-------------------------------------------
        row = sheet.createRow(lastRow++);
        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Num pigs examined");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"Round");

        for(int i = 0; i < (sim.intRounds + 1); i++)
        {

        }



    }

    //====================================================
    public void getFinalHTandPC()
    {
        int statsHT = 0;
        int statsPC = 0;
        int statsHTNorm = 0;
        int statsPCNorm = 0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            if(h.traveling || h.strangerTraveler)continue;

            statsHTNorm++;

            if(h.tapewormMature)statsHT++;

        }
        sim.avgSimPrevHTFinal = sim.avgSimPrevHTFinal + (double)statsHT/(double)statsHTNorm;

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(pig.imported || pig.isAPiglet())continue;

            statsPCNorm++;

            if(pig.numCysts > 0)statsPC++;
        }
        sim.avgSimPrevPCFinal = sim.avgSimPrevPCFinal + (double)statsPC/(double)statsPCNorm;

    }

    //====================================================
    public void writeFinalHTXls()
    {
        //erase_file();

        //if(sim.extendedOutput)System.out.println("================================================");
        //if(sim.extendedOutput)System.out.println ("Writing  outputs ..............................");
        //if(sim.extendedOutput)System.out.println(" ");

        HSSFSheet sheet;

        sheet = workbookOutStats.getSheet("finalHT");
        //If the sheet !exists a new sheet is created --------- 
        if(sheet == null)sheet = workbookOutStats.createSheet("finalHT");

        workbookOutStats.setSheetOrder(statsSheet, 0);

        Cell cell = null;

        int lastRow = sheet.getLastRowNum();

        //if(sim.extendedOutput)System.out.println("Last row:" + lastRow);
        //lastRow++;

        Row row = sheet.createRow(lastRow++);

        int cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs final HT"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim final HT"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim final PC"));


        row = sheet.createRow(lastRow++);
        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((Double)obsPrevHTFinal);

        cell = row.createCell(cellnum++);
        cell.setCellValue((Double)sim.avgSimPrevHTFinal/(double)numInterventionsDone);

        cell = row.createCell(cellnum++);
        cell.setCellValue((Double)sim.avgSimPrevPCFinal/(double)numInterventionsDone);
    }


    //====================================================
    public void writeSeroInctoXlsForExtensions()
    {
        //erase_file();

        //if(sim.extendedOutput)System.out.println("================================================");
        //if(sim.extendedOutput)System.out.println ("Writing  outputs ..............................");
        //if(sim.extendedOutput)System.out.println(" ");

        HSSFSheet sheet;

        sheet = workbookOutStats.getSheet("seroInc for extensions");
        //If the sheet !exists a new sheet is created --------- 
        if(sheet == null)sheet = workbookOutStats.createSheet("seroInc for extensions");

        workbookOutStats.setSheetOrder(statsSheet, 1);

        Cell cell = null;

        int lastRow = sheet.getLastRowNum();

        //if(sim.extendedOutput)System.out.println("Last row:" + lastRow);
        //lastRow++;

        //-------------------------------------------------
        Row row = sheet.createRow(lastRow++);

        int cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"round");

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue((Integer)i);
        }

        //-------------------------------------------------
        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs piglets seroprevalence"));

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.obsSeroPrevalencePigletsRounds.get(i));
        }

        //-------------------------------------------------
        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim piglets seroprevalence"));

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroPrevalencePigletsRounds.get(i)/(double)numInterventionsDone);
        }

        //-------------------------------------------------
        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim pigs cohorts seroprevalence"));

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroPrevalencePigsCohortsRounds.get(i)/(double)numInterventionsDone);
        }



        //-------------------------------------------------
        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs pigs seroprevalence"));

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.obsSeroPrevalencePigsRounds.get(i));
        }

        //-------------------------------------------------
        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim pigs seroprevalence"));

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroPrevalencePigsRounds.get(i)/(double)numInterventionsDone);
        }

        //-------------------------------------------------
        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs seroincidence"));

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
           cell.setCellValue((Double)sim.obsSeroIncidencePigsRounds.get(i));
        }

        cell = row.createCell(cellnum++);
        cell.setCellValue((Double)obsPrevHTFinal);

        //-------------------------------------------------
        row = sheet.createRow(lastRow++);

        cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim seroincidence"));

        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroIncidencePigsRounds.get(i)/(double)numInterventionsDone);
        }

    }

    //====================================================
    public void writeSeroInctoXls(HSSFWorkbook wb)
    {
        //erase_file();

        //if(sim.extendedOutput)System.out.println("================================================");
        //if(sim.extendedOutput)System.out.println ("Writing  outputs ..............................");
        //if(sim.extendedOutput)System.out.println(" ");

        HSSFSheet sheet;

        sheet = wb.getSheet("seroInc");
        //If the sheet !exists a new sheet is created --------- 
        if(sheet == null)sheet = wb.createSheet("seroInc");

        //wb.setSheetOrder(statsSheet, 0);

        Cell cell = null;

        int lastRow = sheet.getLastRowNum();

        //if(sim.extendedOutput)System.out.println("Last row:" + lastRow);
        //lastRow++;

        Row row = sheet.createRow(lastRow++);

        int cellnum = 0;

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)"round");

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs piglets seroprevalence"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim piglets seroprevalence"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs pigs seroprevalence"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim pigs seroprevalence"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim pigs cohorts seroprevalence"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs seroincidence"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim seroincidence"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " obs final HT"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim final HT"));

        cell = row.createCell(cellnum++);
        cell.setCellValue((String)(sim.villageName + " sim final PC"));




        for(int i = 1; i <= sim.seroNRounds; i++)
        {
            row = sheet.createRow(lastRow++);
            cellnum = 0;

            cell = row.createCell(cellnum++);
            cell.setCellValue((Integer)i);

            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.obsSeroPrevalencePigletsRounds.get(i));

            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroPrevalencePigletsRounds.get(i)/(double)numInterventionsDone);

            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.obsSeroPrevalencePigsRounds.get(i));

            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroPrevalencePigsRounds.get(i)/(double)numInterventionsDone);

            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroPrevalencePigsCohortsRounds.get(i)/(double)numInterventionsDone);

            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.obsSeroIncidencePigsRounds.get(i));

            cell = row.createCell(cellnum++);
            cell.setCellValue((Double)sim.avgInterSeroIncidencePigsRounds.get(i)/(double)numInterventionsDone);

            if(i == sim.seroNRounds)
            {
                cell = row.createCell(cellnum++);
                cell.setCellValue((Double)obsPrevHTFinal);

                cell = row.createCell(cellnum++);
                cell.setCellValue((Double)sim.avgSimPrevHTFinal/(double)numInterventionsDone);

                cell = row.createCell(cellnum++);
                cell.setCellValue((Double)sim.avgSimPrevPCFinal/(double)numInterventionsDone);
            }
            else
            {
                cell = row.createCell(cellnum++);
                cell.setCellValue((Double)0.0);

                cell = row.createCell(cellnum++);
                cell.setCellValue((Double)0.0);

                cell = row.createCell(cellnum++);
                cell.setCellValue((Double)0.0);
            }



        }
    }


    //====================================================
    public void setEfficacies()
    {
        sim.niclosamideTreatEff = 1.0;
        sim.elisaSens = 1.0;

        sim.oxfTreatEff = 1.0;

        for(int i = 0; i < 9; i++)
        {
            pigTreatPartRounds.set(i, 1.0);
        }


    }

    /*
    //====================================================
    public void createGraphSeroInc()
    {
        HSSFSheet sheet;

        sheet = workbookOutStats.getSheet("seroInc");

        int nCol = sheet.getRow(0).getLastCellNum();


        Drawing drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);

        Chart chart = drawing.createChart(anchor);

        //ChartLegend legend = chart.getOrCreateLegend();

        //legend.setPosition(LegendPosition.TOP_RIGHT);

        LineChartData data = chart.getChartDataFactory().createLineChartData();

        // Use a category axis for the bottom axis.

        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);

        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);

        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, nCol - 1));

        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, nCol - 1));

        ChartDataSource<Number> ys2 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, nCol - 1));


        data.addSeries(xs, ys1);

        data.addSeries(xs, ys2);

        chart.plot(data, bottomAxis, leftAxis);

    }
    */

    //====================================================
    public void calculatePerSeroPrevalencesnPrint()
    {
        perpigletsSeroPrevalenceSim = 0;
        perpigletsSeroPrevalenceSimNorm = 0;

        perSeroPrevalenceSimAgeTransition = 0;
        perSeroPrevalenceSimAgeTransitionNorm = 0;

        perpigsYoungSeroPrevalenceSim = 0;
        perpigsYoungSeroPrevalenceSimNorm = 0;

        perpigsAdultSeroPrevalenceSim = 0;
        perpigsAdultSeroPrevalenceSimNorm = 0;

        double piglets = 0;
        double pigletsNorm = 0;

        double pigs = 0;
        double pigsNorm = 0;

        //pigletsSeroPrevalenceSim = 0.0;
        //pigletsSeroPrevalenceSimNorm = 0.0;

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(p.imported)continue;

            //calculate simulated observables to be compared with observed data
            if(p.isAgePiglet())
            {
                if(p.seropositive)perpigletsSeroPrevalenceSim++;
                perpigletsSeroPrevalenceSimNorm++;
            }

            if(p.isAgeTransition())
            {
                if(p.seropositive)perSeroPrevalenceSimAgeTransition++;
                perSeroPrevalenceSimAgeTransitionNorm++;
            }
 
            if(p.isAgeYoungPig())
            {
                if(p.seropositive)perpigsYoungSeroPrevalenceSim++;
                perpigsYoungSeroPrevalenceSimNorm++;
            }

            if(p.isAgeAdultPig())
            {
                if(p.seropositive)perpigsAdultSeroPrevalenceSim++;
                perpigsAdultSeroPrevalenceSimNorm++;
            }
 
            //if(p.age> 523)
            //{
            //    p.printResume();
            //    p.household.printResume();
            //}

            //if(p.isAPigletSeroInc())
            //{
            //    if(p.seropositive)piglets++;
            //    pigletsNorm++;
            //}
            //else
            //{
            //    if(p.seropositive)pigs++;
            //    pigsNorm++;
            //}

            //simPrevalenceByAgePigsNorm.set(p.age, (simPrevalenceByAgePigsNorm.get(p.age) + 1)); 
            //if(p.seropositive)simPrevalenceByAgePigs.set(p.age, (simPrevalenceByAgePigs.get(p.age) + 1)); 

        }

        /*
        //------
        System.out.println("------------------------------");
        System.out.println(" ");
        System.out.println("piglets norm: " + perpigletsSeroPrevalenceSimNorm);
        System.out.println("piglets num seropos: " + perpigletsSeroPrevalenceSim);
        System.out.println(" ");
        System.out.println("pigs norm: " + perpigsSeroPrevalenceSimNorm);
        System.out.println("pigs num seropos: " + perpigsSeroPrevalenceSim);
       
        System.out.println("age transition norm: " + perSeroPrevalenceSimAgeTransitionNorm);
        System.out.println("age transition num seropos: " + perSeroPrevalenceSimAgeTransition);
        */

        if(perpigsYoungSeroPrevalenceSimNorm != 0)perpigsYoungSeroPrevalenceSim = perpigsYoungSeroPrevalenceSim/perpigsYoungSeroPrevalenceSimNorm;
        else perpigsYoungSeroPrevalenceSim = 0.0;

        if(perpigsAdultSeroPrevalenceSimNorm != 0)perpigsAdultSeroPrevalenceSim = perpigsAdultSeroPrevalenceSim/perpigsAdultSeroPrevalenceSimNorm;
        else perpigsAdultSeroPrevalenceSim = 0.0;

        if(perpigletsSeroPrevalenceSimNorm != 0)perpigletsSeroPrevalenceSim = perpigletsSeroPrevalenceSim/perpigletsSeroPrevalenceSimNorm;
        else perpigletsSeroPrevalenceSim = 0.0;

        if(perSeroPrevalenceSimAgeTransitionNorm != 0)perSeroPrevalenceSimAgeTransition = perSeroPrevalenceSimAgeTransition/perSeroPrevalenceSimAgeTransitionNorm;
        else perSeroPrevalenceSimAgeTransition = 0.0;

        //double tmp = 0.0; 
        //if(pigletsNorm != 0)tmp = piglets/pigletsNorm;
        //seroIncs.set(0, (tmp + seroIncs.get(0)));

        //tmp = 0.0; 
        //if(pigsNorm != 0)tmp = pigs/pigsNorm;
        //seroIncs.set(1, (tmp + seroIncs.get(1)));

        /*
        //-----
        System.out.println(" ");
        System.out.println("piglets seroprevalence: " + perpigletsSeroPrevalenceSim);
        System.out.println("pigs seroprevalence: " + perpigsSeroPrevalenceSim);
        System.out.println("age transition seroprevalence: " + perSeroPrevalenceSimAgeTransition);
        */

        sim.perseroPrevalencePigletsNorm++; 
        sim.perseroPrevalencePiglets = sim.perseroPrevalencePiglets + perpigletsSeroPrevalenceSim; 

        sim.perseroPrevalencePigsYoungNorm++; 
        sim.perseroPrevalencePigsYoung = sim.perseroPrevalencePigsYoung + perpigsYoungSeroPrevalenceSim; 

        sim.perseroPrevalencePigsAdultNorm++; 
        sim.perseroPrevalencePigsAdult = sim.perseroPrevalencePigsAdult + perpigsAdultSeroPrevalenceSim; 

        sim.perseroPrevalenceAgeTransitionNorm++; 
        sim.perseroPrevalenceAgeTransition = sim.perseroPrevalenceAgeTransition + perSeroPrevalenceSimAgeTransition; 


    }

    //====================================================
    public void accumulateStatsOut()
    {
        if(sim.extendedOutput)System.out.println("Accumulate stats interventions");

        intXlsDataNorm++;

        //System.out.println("num interventions done: " + numInterventionsDone);

        if(numInterventionsDone == 1)
        {
            //intXlsData =  sim.weeklyData;
            intXlsData = new ArrayList<Object[]>(sim.weeklyData);
            //System.out.println("sim.weeklyData first: " + sim.weeklyData.get(1));
            //System.out.println("sim.weeklyData first: " + sim.weeklyData.get(sim.weeklyData.size() - 1));
            //System.exit(0);

            sim.weeklyData = new ArrayList<Object[]>();
            sim.statistics.initXlsData();

            return;
        }

        List<Double> tmp = new ArrayList<Double>();

        /*
        System.out.println("=======================================================");
        System.out.println("=======================================================");
        System.out.println("=======================================================");
        System.out.println("=======================================================");
        System.out.println("------------------------------------");
        System.out.println("weeklyData size: " + sim.weeklyData.size());
        System.out.println("weeklyData size: " + intXlsData.size());
        int len = sim.weeklyData.get(1).length - 1;
        System.out.println("sim.weeklyData: " + sim.weeklyData.get(1)[len]);
        System.out.println("sim.weeklyData: " + sim.weeklyData.get(sim.weeklyData.size() - 1)[len]);

        System.out.println("intXlsData: " + intXlsData.get(1)[len]);
        System.out.println("intXlsData: " + intXlsData.get(intXlsData.size() - 1)[len]);
        */

        for(int i = 0; i < sim.weeklyData.size(); i++)
        {
            if(i == 0)
            {
                //titles = (Object)sim.weeklyData.get(i);
                continue;
            }

            Object[] objNow = (Object[])sim.weeklyData.get(i);
            Object[] objData = (Object[])intXlsData.get(i);

            for(int j = 0; j < objNow.length; j++)
            {
                //System.out.println(i + " " + j);
                objData[j] = (Object)((double)objData[j] + (double)objNow[j]); 
            }

            intXlsData.set(i, objData);
        }
        //System.out.println(" ");
        //System.out.println("intXlsData: " + intXlsData.get(1)[len]);
        //System.out.println("intXlsData: " + intXlsData.get(intXlsData.size() - 1)[len]);

        //System.out.println("=======================================================");
        //System.out.println("=======================================================");
        //System.out.println("=======================================================");
        //System.out.println("=======================================================");

        sim.weeklyData = new ArrayList<Object[]>();
        sim.statistics.initXlsData();
    }



}


