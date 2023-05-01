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
import java.text.DecimalFormat;
import java.util.*;

import sim.field.grid.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.vividsolutions.jts.geom.Point;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;


public class Statistics implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;
    public CystiAgentsWorld simW;

    int count_remove = 0;
    public static int month = 0;
    public static int day = 0;
    public static int year = 0;
    public boolean write = false;

    double malariaObserved = 0.0;

    public int month_old = 0;
    public int year_old = 0;

    public Stoppable stopper;

    public int now = 0;

    public double humanTaeniasisPrev = 0.0;
    public double pigHighlyInfectedPrev = 0.0;
    public double pigLightlyInfectedPrev = 0.0;
    public double pigInfectedPrev = 0.0;
    public double pigInfectedPrevCysts = 0.0;
    public double pigsSeroPrevalenceSim = 0.0;
    public double pigsSeroPrevalenceSimNorm = 0.0;
    public double pigletsSeroPrevalenceSim = 0.0;
    public double pigletsSeroPrevalenceSimNorm = 0.0;
    public double pigsSeroIncidenceSim = 0.0;
    public double pigsSeroIncidenceSimNorm = 0.0;
    public int humansInTheVillage = 0;
    public double avgImmunityC = 0.0;
    public double avgImmunityO = 0.0;
    public double avgImmunityI = 0.0;
    public int numHumansTraveling = 0;
    public int numHumansStrangerTravelers = 0;
    public int numHumansStrangerTravelersInfected = 0;

    public double perpigletsSeroPrevalenceSim = 0.0;
    public double perpigletsSeroPrevalenceSimNorm = 0.0;
    public double perSeroPrevalenceSimAgeTransition = 0.0;
    public double perSeroPrevalenceSimAgeTransitionNorm = 0.0;
    public double perpigsYoungSeroPrevalenceSim = 0.0;
    public double perpigsYoungSeroPrevalenceSimNorm = 0.0;
    public double perpigsAdultSeroPrevalenceSim = 0.0;
    public double perpigsAdultSeroPrevalenceSimNorm = 0.0;

    /*
    public double pigHighlyInfectedPrevCloseTapeworm = 0.0;
    public double pigHighlyInfectedPrevNoCloseTapeworm = 0.0;
    public double statsPigHighlyInfectedPrevNoCloseTapeworm = 0.0;
    public double statsPigHighlyInfectedPrevCloseTapeworm = 0.0;

    public double pigLightlyInfectedPrevCloseTapeworm = 0.0;
    public double pigLightlyInfectedPrevNoCloseTapeworm = 0.0;
    public double statsPigLightlyInfectedPrevNoCloseTapeworm = 0.0;
    public double statsPigLightlyInfectedPrevCloseTapeworm = 0.0;

    public double fractPigsCloseTapeworm = 0.0;
    */

    public double numInfectedHumans = 0.0;
    public double numHighlyInfectedPigs = 0.0;
    public double numLightlyInfectedPigs = 0.0;
    public double numInfectedPigsCysts = 0.0;

    public double numHousehold = 0.0;
    public double numHouseholdWithLatrine = 0.0;

    public int numEggs = 0;
    public int numProglottids = 0;

    public HashMap<Integer, Double> pigCystsHistoLocal = new HashMap <Integer, Double>();
    public HashMap<Integer, Double> pigCystsHistoProgLocal = new HashMap <Integer, Double>();

    public double avgNumCystsNoNecro = 0.0;
    public double avgDegeneratedCystsCNoNecro = 0.0;
    public double avgDegeneratedCystsINoNecro = 0.0;
    public double avgNumCystsFromProglottidsNoNecro = 0.0;
    public double avgNumCystsFromEggsNoNecro = 0.0;

    //CystiHumans parameters
    //not emigranits
    public double NCCPrevalence = 0.0;
    public double NCCPrevalence12more = 0.0;
    public double NCCPrevalence20more = 0.0;
    public double NCCPrevalence18more = 0.0; //  
    public double share1CystInAdultNCC = 0.0; // GBRicaPlaya
    public double share11ormoreCystsInAdultNCC = 0.0; // GBRicaPlaya
    public List<Integer> humanNCCCases = new ArrayList<Integer>();
    public List<Double> humanNCCShareByNumberOfLesion = new ArrayList<Double>();

    public List<Integer> humanNCCByAge = new ArrayList<Integer>();
    public List<Double> humanNCCPrevByAge = new ArrayList<Double>();

    public List<Integer> humanByAge = new ArrayList<Integer>();

    public double NCCRelatedEpiPrevalence = 0.0;
    public double NCCRelatedAEpiPrevalence = 0.0;
    public double NCCRelatedICHPrevalence = 0.0;

    //public double newICHoverNewAE = 0.0;
    public double shareNCCcasesNonCalcified = 0.0;
    public double shareofExParinNCCcases = 0.0;
    public double shareofNCCcaseswithEpi = 0.0;
    public double shareofNCCcaseswithAE = 0.0; // GBPIAE
    public double shareofParenchymalinICH = 0.0;
    public double shareofEpiNCCcalcifiedWithActiveEpi = 0.0;
    public double shareofEpiNCCcalcifiedWithActiveEpiCutOff = 0.0; // GB11mars
    public double shareofEpiNCCcalcifiedWithActiveEpiMoyano = 0.0; //  
    public double shareofEpiNCCnoncalcWithActiveEpiMoyano = 0.0; // GB23mars
    public double shareofAEcasesThatAreNonCalcified = 0.0;
    public double shareofAEcasesThatAreNonCalcifiedMoyano = 0.0; //  
    public double shareofAEcasesUnderTreatment = 0.0;

    public double shareWithDegeneratedinNonCalcified; // GB26avril
    public double shareWithViableinNonCalcified; // GB26avril

    //emigrants
    public double NCCPrevalenceEmigrants = 0.0;
    public List<Integer> humanNCCCasesEmigrants = new ArrayList<Integer>();
    public List<Double> humanNCCShareByNumberOfLesionEmigrants = new ArrayList<Double>();

    public List<Integer> humanNCCByAgeEmigrants = new ArrayList<Integer>();
    public List<Double> humanNCCPrevByAgeEmigrants = new ArrayList<Double>();

    public List<Integer> humanByAgeEmigrants = new ArrayList<Integer>();

    public double NCCRelatedEpiPrevalenceEmigrants = 0.0;
    public double NCCRelatedAEpiPrevalenceEmigrants = 0.0;
    public double NCCRelatedICHPrevalenceEmigrants = 0.0;

    public double newICHoverNewAEEmigrants = 0.0;
    public double shareNCCcasesNonCalcifiedEmigrants = 0.0;
    public double shareofExParinNCCcasesEmigrants = 0.0;
    public double shareofNCCcaseswithEpiEmigrants = 0.0;
    public double shareofParenchymalinICHEmigrants = 0.0;
    public double shareofEpiNCCcalcifiedWithActiveEpiEmigrants = 0.0;
    public double shareofAEcasesThatAreNonCalcifiedEmigrants = 0.0;
    public double shareofAEcasesUnderTreatmentEmigrants = 0.0;

    public double nICHCases = 0; //  
    public double nICHCasesEmigrants = 0; //  
    public double nParInICH = 0; //  

    public double adultNCCPevalenceCT = 0; // gmb
    public double shareEverNCC = 0; // GBPIAE
    //public double shareEverNCCdisa = 0; // GBPIAE
    public double shareEverNCC60 = 0; // GBPIAE
    public int nActiveLesionCases = 0; // GBPIAE
    public int nAECaseswithNonCalcified = 0; // GBPIAE
    public int nEpiCasesNonCalcified = 0; // GBPIAE
    public int nEpiCasesVisible = 0; // GBPIAE
    public int nActiveEpiCasesVisible = 0; // GBPIAE
    public int nIAECalcified = 0; // GBPIAE
    public double epiPrevalenceinAdultNCCCases = 0.0; // GBRicaPlaya
    //public int nIAECalcifiedEverdisa = 0; // GBPIAE
    //public double shareAEinNonCalcified = 0; // GB16mars

    public Boolean firstCohort = true;

    public List<Double> simPrevalenceByAgePigs = new ArrayList<Double>();
    public List<Double> simPrevalenceByAgePigsNorm = new ArrayList<Double>();
    public List<Double> simPrevalenceByAgePigsHisto = new ArrayList<Double>();

    public List<Double> simPrevalenceByAgeHumans = new ArrayList<Double>();
    public List<Double> simPrevalenceByAgeHumansNorm = new ArrayList<Double>();
    public List<Double> simPrevalenceByAgeHumansHisto = new ArrayList<Double>();

    int totPigs = 0;
    int maxHumanAge = 0;

    int countInc = -100;
    int perInc = 100;
    int perIncTimer = 1;
    public List<Double> seroIncs = new ArrayList<Double>();
    public int seroIncNumSteps = 0;

    //====================================================
    public Statistics(CystiAgents pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;

        simW = sim.simW;

        if(!sim.doInterventions)initXlsData();

        sim.schedule.scheduleRepeating(1.0, 15, this);

        for(int i = 0; i < 523; i++)
        {
            simPrevalenceByAgePigs.add(0.0);
            simPrevalenceByAgePigsNorm.add(0.0);
            simPrevalenceByAgePigsHisto.add(0.0);
        }

        //maxHumanAge = (int)Math.round(120 * sim.weeksInAYear);
        maxHumanAge = 12000;
        for(int i = 0; i < maxHumanAge; i++)
        {
            simPrevalenceByAgeHumans.add(0.0);
            simPrevalenceByAgeHumansNorm.add(0.0);
            simPrevalenceByAgeHumansHisto.add(0.0);
        }

        if(sim.seroincidenceCohortsPer != 0)
        {
            seroIncNumSteps = sim.seroincidenceCohortsPer * sim.seroincidenceCohortsNumRounds; 
            //System.out.println (sim.seroincidenceCohortsPer);
            //System.out.println (sim.seroincidenceCohortsNumRounds);
            //System.out.println (seroIncNumSteps);
            perInc = sim.nPrint;
            //System.exit(0);
        }


    }

    //====================================================
    public void step(SimState pstate)
    {
        now = (int)state.schedule.getTime();
        //System.out.println(sim.villageName + " Total Number of meat Portions in the village: " + sim.meatPortionsBag.size());

        //System.out.println ("Statistics starts");
        //System.out.println ("Day of Week: " + sim.utils.getDayOfWeek(sim));

        if(!sim.burnin)sim.numWeeks++;

        //System.out.println ("---");
        //System.out.println (sim.seroincidenceCohortsPer);
        //System.out.println (now);
        //System.out.println (now % sim.seroincidenceCohortsPer);


        //pitxi qui histo age prev weekly and not every 4 months
        //for(int i = 0; i < sim.pigsBag.size(); i++)
        //{
        //    Pig p = (Pig)sim.pigsBag.get(i);
        //    simPrevalenceByAgePigsNorm.set(p.age, (simPrevalenceByAgePigsNorm.get(p.age) + 1)); 
        //    if(p.seropositive)simPrevalenceByAgePigs.set(p.age, (simPrevalenceByAgePigs.get(p.age) + 1)); 
        //}

        //calculate simulated serology data and upgrade seroincidence cohorts
        //if(!sim.burnin && sim.seroincidenceCohortsPer != 0)serologyCalculationsnPrint();

        //calculate simulated serology data anbd upgrade seroincidence cohorts
        if(     !sim.burnin
                && sim.seroincidenceCohortsPer != 0 
                && (now % sim.seroincidenceCohortsPer) == 0
                && !sim.doInterventions
                )
        {
            serologyCalculations();
        }

        if((now % sim.nPrint) == 0 && !sim.burnin)
        {
            sim.numWeeksPrint++;
            //System.exit(0);
            calcStats();

            sumPigHisto();
            sumPigHistoProg();

            sim.infectedHumansPrevalence = sim.infectedHumansPrevalence + humanTaeniasisPrev;
            sim.lightInfectedPigsPrevalence = sim.lightInfectedPigsPrevalence + pigLightlyInfectedPrev;
            sim.heavyInfectedPigsPrevalence = sim.heavyInfectedPigsPrevalence + pigHighlyInfectedPrev;
            sim.infectedPigsPrevalence = sim.infectedPigsPrevalence + pigInfectedPrev;
            sim.infectedPigsPrevalenceCysts = sim.infectedPigsPrevalenceCysts + pigInfectedPrevCysts;

            if(sim.strangerTravelersBag.size() != 0)sim.prevHumansStrangerTravelers = sim.prevHumansStrangerTravelers  + (double)numHumansStrangerTravelersInfected/(double)sim.strangerTravelersBag.size();
            else sim.prevHumansStrangerTravelers = sim.prevHumansStrangerTravelers  + 0.0;

            sim.avgImmunityC = sim.avgImmunityC + avgImmunityC;
            sim.avgImmunityO = sim.avgImmunityO + avgImmunityO;
            sim.avgImmunityI = sim.avgImmunityI + avgImmunityI;

            sim.avgDegeneratedCystsCTimestep = sim.avgDegeneratedCystsCTimestep / (double)sim.pigsBag.size();
            sim.avgDegeneratedCystsITimestep = sim.avgDegeneratedCystsITimestep / (double)sim.pigsBag.size();

            sim.avgDegeneratedCystsC = sim.avgDegeneratedCystsC + sim.avgDegeneratedCystsCTimestep;
            sim.avgDegeneratedCystsI = sim.avgDegeneratedCystsI + sim.avgDegeneratedCystsITimestep;

            sim.avgNumCystsFromProglottidsTimestep = sim.avgNumCystsFromProglottidsTimestep / (double)sim.pigsBag.size();
            sim.avgNumCystsFromEggsTimestep = sim.avgNumCystsFromEggsTimestep / (double)sim.pigsBag.size();

            sim.avgNumCystsFromProglottids = sim.avgNumCystsFromProglottids + sim.avgNumCystsFromProglottidsTimestep;
            sim.avgNumCystsFromEggs = sim.avgNumCystsFromEggs + sim.avgNumCystsFromEggsTimestep;


            sim.avgNumCystsTimestep = sim.avgNumCystsTimestep / (double)sim.pigsBag.size();
            sim.avgNumCysts = sim.avgNumCysts + sim.avgNumCystsTimestep;

            //if(humanTaeniasisPrev == 0 && pigInfectedPrev == 0)System.exit(0);

            /*
            sim.heavyInfectedPigsPrevalenceCloseTapeworm = sim.heavyInfectedPigsPrevalenceCloseTapeworm + pigHighlyInfectedPrevCloseTapeworm;
            sim.heavyInfectedPigsPrevalenceNoCloseTapeworm = sim.heavyInfectedPigsPrevalenceNoCloseTapeworm + pigHighlyInfectedPrevNoCloseTapeworm;

            sim.lightInfectedPigsPrevalenceCloseTapeworm = sim.lightInfectedPigsPrevalenceCloseTapeworm + pigLightlyInfectedPrevCloseTapeworm;
            sim.lightInfectedPigsPrevalenceNoCloseTapeworm = sim.lightInfectedPigsPrevalenceNoCloseTapeworm + pigLightlyInfectedPrevNoCloseTapeworm;

            sim.avgFractPigsCloseTapeworm = sim.avgFractPigsCloseTapeworm + fractPigsCloseTapeworm;
            */

            //Count the number of meat portions and their distribution
            sim.numMeatPortionsSold = sim.numMeatPortionsSold + sim.meatPortionsBag.size();
            sim.totNumMeatPortions = sim.numMeatPortionsSold + sim.numMeatPortionsDistributedToHouseholds + sim.numMeatPortionsConsumedByOwners;

            sim.avgNumCystsNoNecro = sim.avgNumCystsNoNecro + avgNumCystsNoNecro;
            sim.avgDegeneratedCystsCNoNecro = sim.avgDegeneratedCystsCNoNecro + avgDegeneratedCystsCNoNecro;
            sim.avgDegeneratedCystsINoNecro = sim.avgDegeneratedCystsINoNecro + avgDegeneratedCystsINoNecro;
            sim.avgNumCystsFromProglottidsNoNecro = sim.avgNumCystsFromProglottidsNoNecro + avgNumCystsFromProglottidsNoNecro;
            sim.avgNumCystsFromEggsNoNecro = sim.avgNumCystsFromEggsNoNecro + avgNumCystsFromEggsNoNecro;

            //System.out.println ("-----------------------------------");
            //System.out.println (sim.meatPortionsBag.size());
            //System.out.println (sim.numMeatPortionsConsumedByOwners);
            //System.out.println (sim.numMeatPortionsDistributedToHouseholds);
            //System.out.println (sim.numMeatPortionsSold);
            //System.out.println (sim.totNumMeatPortions);
        }

        if(sim.cystiHumans & !sim.burnin)
        {   
            //if((now % sim.nPrint) == 0 && !sim.burnin)
            //{ // GB16mars
            //    //Human humanoid = (Human)sim.humansBag.get(53); // GB16mars
            //    humanoid.printResume();
            //} // GB16mars */

            //  sim.sumShareEverTaenia = sim.sumShareEverTaenia + sim.shareEverTaenia; // GB17mars
            //  sim.averageShareEverTaenia = sim.sumShareEverTaenia / (double)sim.numWeeks; // GB17mars
            //  sim.sumShareEverTaenia60 = sim.sumShareEverTaenia60 + sim.shareEverTaenia60; // GB17mars
            //  sim.averageShareEverTaenia60 = sim.sumShareEverTaenia60 / (double)sim.numWeeks; // GB17mars

            if(share1CystInAdultNCC>-1) // GBRicaPlaya
            { // GBRicaPlaya
                sim.weeksAdultsWithNCC++; // GBRicaPlaya
                sim.sumShareAdult1CystInNCC = sim.sumShareAdult1CystInNCC + share1CystInAdultNCC; // GBRicaPlaya
                sim.sumShareAdult11moreCystInNCC = sim.sumShareAdult11moreCystInNCC + share11ormoreCystsInAdultNCC; // GBRicaPlaya
                sim.averageShareAdult1CystInNCC = sim.sumShareAdult1CystInNCC / (double)sim.weeksAdultsWithNCC; // GBRicaPlaya
                sim.averageShareAdult11moreCystInNCC = sim.sumShareAdult11moreCystInNCC / (double)sim.weeksAdultsWithNCC; // GBRicaPlaya
                sim.sumEpiPrevalenceinAdultNCCCases = sim.sumEpiPrevalenceinAdultNCCCases + epiPrevalenceinAdultNCCCases; // GBRicaPlaya
                sim.averageEpiPrevalenceinAdultNCCCases = sim.sumEpiPrevalenceinAdultNCCCases / (double)sim.weeksAdultsWithNCC; // GBRicaPlaya
            } // GBRicaPlaya

            sim.averageShareofParenchymalinICHCyst = (double)sim.incidentParICHCyst/
                ((double)sim.incidentParICHCyst +(double)sim.incidentExParICHCyst);
            sim.sumNCCPrevalence = sim.sumNCCPrevalence + NCCPrevalence;
            sim.sumNCCPrevalence12more = sim.sumNCCPrevalence12more + NCCPrevalence12more;
            sim.sumNCCPrevalence20more = sim.sumNCCPrevalence20more + NCCPrevalence20more;
            sim.averageNCCPrevalence20more = sim.sumNCCPrevalence20more/(double)sim.numWeeks;
            sim.sumNCCPrevalence18more = sim.sumNCCPrevalence18more + NCCPrevalence18more;
            sim.sumAdultNCCPevalenceCT = sim.sumAdultNCCPevalenceCT + adultNCCPevalenceCT;
            sim.averageNCCPrevalence18more = sim.sumNCCPrevalence18more/(double)sim.numWeeks;
            sim.averageAdultNCCPevalenceCT = sim.sumAdultNCCPevalenceCT/(double)sim.numWeeks;

            if(shareofEpiNCCnoncalcWithActiveEpiMoyano > -1) // GB23mars
            { // GB23mars
                sim.sumShareofEpiNCCnoncalcWithActiveEpiMoyano = sim.sumShareofEpiNCCnoncalcWithActiveEpiMoyano + shareofEpiNCCnoncalcWithActiveEpiMoyano; // GB23mars
                sim.weeksShareofEpiNCCnoncalcWithActiveEpiMoyano++; // GB23mars
                sim.averageShareofEpiNCCnoncalcWithActiveEpiMoyano = // GB23mars
                    sim.sumShareofEpiNCCnoncalcWithActiveEpiMoyano / (double)sim.weeksShareofEpiNCCnoncalcWithActiveEpiMoyano; // GB23mars
            } // GB23mars

            if(humanNCCShareByNumberOfLesion.size() > 0 && NCCPrevalence > 0)
            {
                sim.sumShare1CystInNCC = sim.sumShare1CystInNCC + humanNCCShareByNumberOfLesion.get(0);
                sim.sumShare2CystsInNCC = sim.sumShare2CystsInNCC + humanNCCShareByNumberOfLesion.get(1);
                sim.sumShare3CystsInNCC = sim.sumShare3CystsInNCC + humanNCCShareByNumberOfLesion.get(2);
                sim.sumShare4CystsInNCC = sim.sumShare4CystsInNCC + humanNCCShareByNumberOfLesion.get(3);
                sim.sumShare5CystsInNCC = sim.sumShare5CystsInNCC + humanNCCShareByNumberOfLesion.get(4);
                sim.averageShare1CystInNCC = sim.sumShare1CystInNCC/(double)sim.numWeeks;
                sim.averageShare2CystsInNCC = sim.sumShare2CystsInNCC/(double)sim.numWeeks;
                sim.averageShare3CystsInNCC = sim.sumShare3CystsInNCC/(double)sim.numWeeks;
                sim.averageShare4CystsInNCC = sim.sumShare4CystsInNCC/(double)sim.numWeeks;
                sim.averageShare5CystsInNCC = sim.sumShare5CystsInNCC/(double)sim.numWeeks;
            }
            sim.sumShareNCCcasesNonCalcified = sim.sumShareNCCcasesNonCalcified + shareNCCcasesNonCalcified;
            sim.sumShareofExParinNCCcases = sim.sumShareofExParinNCCcases + shareofExParinNCCcases;
            sim.sumShareofNCCcaseswithEpi = sim.sumShareofNCCcaseswithEpi + shareofNCCcaseswithEpi;
            sim.averageShareNCCcasesNonCalcified = sim.sumShareNCCcasesNonCalcified /(double)sim.numWeeks;
            sim.averageShareofExParinNCCcases = sim.sumShareofExParinNCCcases / (double)sim.numWeeks;
            sim.averageShareofNCCcaseswithEpi = sim.sumShareofNCCcaseswithEpi / (double)sim.numWeeks;

            if(shareWithViableinNonCalcified!=-1.0) // GB26 avril
            { // GB26 avril
                sim.sumShareWithViableinNonCalcified = sim.sumShareWithViableinNonCalcified // GB26 avril
                    + shareWithViableinNonCalcified;// GB26 avril
                sim.sumShareWithDegeneratedinNonCalcified = sim.sumShareWithDegeneratedinNonCalcified // GB26 avril
                    + shareWithDegeneratedinNonCalcified;// GB26 avril

                sim.weeksShareWithViableinNonCalcified++;// GB26 avril

                sim.averageShareWithViableinNonCalcified = sim.sumShareWithViableinNonCalcified / // GB26 avril
                    sim.weeksShareWithViableinNonCalcified;// GB26 avril
                sim.averageShareWithDegeneratedinNonCalcified = sim.sumShareWithDegeneratedinNonCalcified / // GB26 avril
                    sim.weeksShareWithViableinNonCalcified;// GB26 avril
            } // GB26 avril



            if(shareofParenchymalinICH!=-1.0)
            {
                sim.sumShareofParenchymalinICH = sim.sumShareofParenchymalinICH + shareofParenchymalinICH ;
                sim.weeksShareofParenchymalinICH++;
                sim.averageShareofParenchymalinICH=sim.sumShareofParenchymalinICH/(double)sim.weeksShareofParenchymalinICH;
            }
            /*
               if(shareAEinNonCalcified!=-1.0) // GB16mars
               { // GB16mars
               sim.sumShareAEinNonCalcified = sim.sumShareAEinNonCalcified + shareAEinNonCalcified; // GB16mars
               sim.weeksShareAEinNonCalcified++; // GB16mars
               sim.averageShareAEinNonCalcified = sim.sumShareAEinNonCalcified/(double)sim.weeksShareAEinNonCalcified; // GB16mars
               } // GB16mars
               */
            if(shareofEpiNCCcalcifiedWithActiveEpi!=-1.0)
            {
                sim.sumShareofEpiNCCcalcifiedWithActiveEpi = sim.sumShareofEpiNCCcalcifiedWithActiveEpi
                    + shareofEpiNCCcalcifiedWithActiveEpi;

                sim.weeksShareofEpiNCCcalcifiedWithActiveEpi++;

                sim.averageShareofEpiNCCcalcifiedWithActiveEpi = sim.sumShareofEpiNCCcalcifiedWithActiveEpi
                    /(double)sim.weeksShareofEpiNCCcalcifiedWithActiveEpi;
            }

            if(shareofEpiNCCcalcifiedWithActiveEpiCutOff!=-1.0) // GB11mars
            { // GB11mars
                sim.sumShareofEpiNCCcalcifiedWithActiveEpiCutOff = sim.sumShareofEpiNCCcalcifiedWithActiveEpiCutOff // GB11mars
                    + shareofEpiNCCcalcifiedWithActiveEpiCutOff; // GB11mars

                sim.weeksShareofEpiNCCcalcifiedWithActiveEpiCutOff++; // GB11mars

                sim.averageShareofEpiNCCcalcifiedWithActiveEpiCutOff = sim.sumShareofEpiNCCcalcifiedWithActiveEpiCutOff // GB11mars
                    /(double)sim.weeksShareofEpiNCCcalcifiedWithActiveEpiCutOff; // GB11mars
            } // GB11mars

            if(shareofAEcasesThatAreNonCalcified!=-1.0)
            {
                sim.sumShareofAEcasesThatAreNonCalcified = sim.sumShareofAEcasesThatAreNonCalcified
                    + shareofAEcasesThatAreNonCalcified;

                sim.weeksShareofAEcasesThatAreNonCalcified++;

                sim.averageShareofAEcasesThatAreNonCalcified = sim.sumShareofAEcasesThatAreNonCalcified /
                    sim.weeksShareofAEcasesThatAreNonCalcified;
            }
            if(shareofEpiNCCcalcifiedWithActiveEpiMoyano!=-1.0)
            { //
                sim.sumShareofEpiNCCcalcifiedWithActiveEpiMoyano = sim.sumShareofEpiNCCcalcifiedWithActiveEpiMoyano
                    + shareofEpiNCCcalcifiedWithActiveEpiMoyano;

                sim.weeksShareofEpiNCCcalcifiedWithActiveEpiMoyano++;

                sim.averageShareofEpiNCCcalcifiedWithActiveEpiMoyano = sim.sumShareofEpiNCCcalcifiedWithActiveEpiMoyano
                    /(double)sim.weeksShareofEpiNCCcalcifiedWithActiveEpiMoyano;
            }
            if(shareofAEcasesThatAreNonCalcifiedMoyano!=-1.0)
            {
                sim.sumShareofAEcasesThatAreNonCalcifiedMoyano = sim.sumShareofAEcasesThatAreNonCalcifiedMoyano
                    + shareofAEcasesThatAreNonCalcifiedMoyano;

                sim.weeksShareofAEcasesThatAreNonCalcifiedMoyano++;

                sim.averageShareofAEcasesThatAreNonCalcifiedMoyano = sim.sumShareofAEcasesThatAreNonCalcifiedMoyano
                    / sim.weeksShareofAEcasesThatAreNonCalcifiedMoyano;
            }
            if(sim.ichOverAE!=-1.0)
            {
                sim.sumIchOverAE = sim.sumIchOverAE + sim.ichOverAE;
                sim.weeksIchOverAE++;
            }


        }


        if((now % sim.nPrint) == 0 && !sim.burnin && !sim.doInterventions)
        {
            //System.exit(0);
            //calcStats();
            //writeStandardOutput();

            writeXlsOutput();
        }

        if(sim.singleRun && (now % sim.nPrint) == 0)
        {
            if(sim.extendedOutput)writeStandardOutput();
        }

        //if((!sim.oInterventions && sim.numWeeks == (sim.numStep - 2 - sim.burninPeriod))
        //|| (sim.doInterventions && sim.numWeeks == (sim.numStep - 2 - sim.burninPeriod - sim.preInterventionsNumStep))
        //            )
        //if(sim.numWeeks == (sim.numStep - 2 - sim.burninPeriod))
        //System.out.println (now + " " + (sim.numStep - 2 + sim.burninPeriod));
        if(now == (sim.numStep - 2))
        {
            if(sim.extendedOutput)System.out.println ("----------------- Writing xls output");
            sim.wXls.writeTimeSeries();

            sim.wXls.writeTimeAveragesCysts();

            //WriteShp wShp = new WriteShp(state);
            //wShp.writeHouseholds();

            if(sim.doInterventions)
            {
                if(sim.interventionsType.equals("test"))
                {
                    sim.interventionsTest.writeSeroInctoXls(sim.wXls.workbook);
                }
                else if(sim.interventionsType.equals("R01"))
                {
                    sim.interventionsR01.writeSeroInctoXls(sim.wXls.workbook);
                    sim.interventionsR01.writeSeroIncValuestoXls(sim.wXls.workbook);
                }
                else if(sim.interventionsType.equals("TTEMP"))
                {
                    sim.interventionsTTEMP.writeSeroInctoXls(sim.wXls.workbook);
                }
                else if(sim.interventionsType.equals("africanSwineFever"))
                {
                    sim.interventionsAfricanSwineFever.writeSeroInctoXls(sim.wXls.workbook);
                }
            }

            if(sim.readPopFromSurveyCensusFile && sim.villageDataset.equals("TTEMP"))
            {
                sim.wXls.writePigCystsHisto();
                sim.wXls.writePigCystsHistoProg();
            }

            sim.wXls.writeToFile();

            //writeHistoPrevByAgePigs();

            //writeHistoPrevByAgeHumans();

            if(sim.extendedOutput)writeStandardOutput();


            if(sim.doInterventions)
            {
                if(sim.interventionsType.equals("test"))
                {
                    sim.interventionsTest.printResume();
                }
                else if(sim.interventionsType.equals("R01"))
                {
                    sim.interventionsR01.printResume();
                }
                else if(sim.interventionsType.equals("TTEMP"))
                {
                    sim.interventionsTTEMP.printResume();
                    sim.interventionsTTEMP.writeStatsToFile();
                }
                else if(sim.interventionsType.equals("africanSwineFever"))
                {
                    sim.interventionsAfricanSwineFever.printResume();
                }
            }



            //System.out.println(sim.villageName + " Fract pigs exposed in: " + (double)sim.nPigsExposedIn/(double)sim.nTotPigs);
            //System.out.println(sim.villageName + " Fract pigs exposed out: " + (double)sim.nPigsExposedOut/(double)sim.nTotPigs);

        }

        //System.out.println(sim.villageName + " Total Number of meat Portions in the village: " + sim.meatPortionsBag.size());
        sim.firstMeatPortionsShuffle = true;
        sim.meatPortionsBag = new Bag();
        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household h = (Household)sim.householdsBag.get(i);
            h.meatPortionsNoInfected = new Bag();
            h.meatPortionsInfected = new Bag();
        }

        sim.avgDegeneratedCystsCTimestep = 0.0;
        sim.avgDegeneratedCystsITimestep = 0.0;

        sim.avgNumCystsFromProglottidsTimestep = 0.0;
        sim.avgNumCystsFromEggsTimestep = 0.0;
        sim.avgNumCystsTimestep = 0.0;

    }//end step

    //====================================================
    public void writeHistoPrevByAgeHumans()
    {
        //calculate prev by ages histo
        double stats = 0;
        for(int i = 0; i < maxHumanAge; i++)
        {
            simPrevalenceByAgeHumansHisto.set(i, simPrevalenceByAgeHumans.get(i)/simPrevalenceByAgeHumansNorm.get(i));
            stats = stats + simPrevalenceByAgeHumansNorm.get(i);
        }

        //if(sim.extendedOutput)System.out.println("sim.nTotpigs: " + sim.nTotPigs);
        //if(sim.extendedOutput)System.out.println("Local totPigs: " + totPigs);
        if(sim.extendedOutput)System.out.println("------------------------------");
        if(sim.extendedOutput)System.out.println("age-in-weeks age-in-years num-humans-per-age-segment num-positive-humans-per-age-segment prop-humans-per-age-segment seroprev-humans-per-age-segment" );
        for(int i = 0; i < maxHumanAge; i++)
        {
            if(simPrevalenceByAgeHumansHisto.get(i) > 0.0)
            {
                if(sim.extendedOutput)System.out.println(i 
                        + " " + i / (double)sim.weeksInAYear 
                        + " " + simPrevalenceByAgeHumansNorm.get(i) 
                        + " " + simPrevalenceByAgeHumans.get(i) 
                        + " " + simPrevalenceByAgeHumansNorm.get(i)/(double)stats  
                        + " " + simPrevalenceByAgeHumansHisto.get(i));
            }

        }
    }

    //====================================================
    public void writeHistoPrevByAgePigs()
    {
        //calculate prev by ages histo
        double stats = 0;
        for(int i = 0; i < 523; i++)
        {
            simPrevalenceByAgePigsHisto.set(i, simPrevalenceByAgePigs.get(i)/simPrevalenceByAgePigsNorm.get(i));
            stats = stats + simPrevalenceByAgePigsNorm.get(i);
        }

        if(sim.extendedOutput)System.out.println("sim.nTotpigs: " + sim.nTotPigs);
        if(sim.extendedOutput)System.out.println("Local totPigs: " + totPigs);
        if(sim.extendedOutput)System.out.println("------------------------------");
        if(sim.extendedOutput)System.out.println("age-in-weeks age-in-months num-pigs-per-age-segment num-seropositive-pigs-per-age-segment prop-pigs-per-age-segment seroprev-pigs-per-age-segment" );
        for(int i = 0; i < 523; i++)
        {
                //if(simPrevalenceByAgePigsHisto.get(i) > 0.0)
                //{
            if(sim.extendedOutput)System.out.println(i
                    + " " + (double)i/sim.weeksInAMonth 
                    + " " + simPrevalenceByAgePigsNorm.get(i) 
                    + " " + simPrevalenceByAgePigs.get(i) 
                    + " " + simPrevalenceByAgePigsNorm.get(i)/(double)stats  
                    + " " + simPrevalenceByAgePigsHisto.get(i));
            //}

        }
    }

    //====================================================
    public void calculatePerSeroPrevalences()
    {
        perpigletsSeroPrevalenceSim = 0;
        perpigletsSeroPrevalenceSimNorm = 0;

        perSeroPrevalenceSimAgeTransition = 0;
        perSeroPrevalenceSimAgeTransitionNorm = 0;

        perpigsYoungSeroPrevalenceSim = 0;
        perpigsYoungSeroPrevalenceSimNorm = 0;

        perpigsAdultSeroPrevalenceSim = 0;
        perpigsAdultSeroPrevalenceSimNorm = 0;

        //pigletsSeroPrevalenceSim = 0.0;
        //pigletsSeroPrevalenceSimNorm = 0.0;

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(p.imported)continue;

            //--------------------------------------------------------------
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
            //--------------------------------------------------------------
 
            //if(p.age> 523)
            //{
            //    p.printResume();
            //    p.household.printResume();
            //}

            simPrevalenceByAgePigsNorm.set(p.age, (simPrevalenceByAgePigsNorm.get(p.age) + 1)); 
            if(p.seropositive)simPrevalenceByAgePigs.set(p.age, (simPrevalenceByAgePigs.get(p.age) + 1)); 

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
    public void serologyCalculations()
    {
        //System.out.println (now + " periodpo");
        if(firstCohort)
        {
            for(int i = 0; i < sim.pigsBag.size(); i++)
            {
                Pig p = (Pig)sim.pigsBag.get(i);

                p.isInTheSeroIncCohortSim = false;

                if(p.isAPigletSeroInc())
                {
                    p.isInTheSeroIncCohortSim = true;
                }
            }

        }

        firstCohort = false;

        addPigletsToSeroincidenceCohort();
        //System.out.println ("Calculating seroincidence");
        calculateSeroStats();
        calculatePerSeroPrevalences();
        //System.out.println ("Updating seroincidence cohorts");
        removePigsFromSeroincidenceCohort();

    }

    //====================================================
    public void serologyCalculationsnPrint()
    {
        //System.out.println (now + " periodpo");
        if(firstCohort)//initialize the seoinc cohort
        {
            for(int i = 0; i < sim.pigsBag.size(); i++)
            {
                Pig p = (Pig)sim.pigsBag.get(i);
                p.isInTheSeroIncCohortSim = false;

                if(p.isAPigletSeroInc())
                {
                    p.isInTheSeroIncCohortSim = true;
                }
            }

            firstCohort = false;
        }

        //inter measurement
        if(countInc < 0 && perIncTimer > 0)
        {

            System.out.println ("In nprint: now: " + now + " perIncTimer: " + perIncTimer + " countInc: " + countInc);
            perIncTimer++;

            if((perIncTimer % perInc) == 0)
            {

                //System.out.println ("epepepepepepepepepe End nprint: now: " + now + " perIncTimer: " + perIncTimer + " countInc: " + countInc);

                countInc = 1;
                perIncTimer = -100;

                seroIncs = new ArrayList<Double>();

                seroIncs.add(0.0);
                seroIncs.add(0.0);
                seroIncs.add(0.0);
            }

            if(countInc < 0)
            {
                addPigletsToSeroincidenceCohort();
                removePigsFromSeroincidenceCohort();
                return;
            }
        }

        if(countInc > 0 && perIncTimer < 0)
        {

            System.out.println ("In record: now: " + now + " perIncTimer: " + perIncTimer + " countInc: " + countInc);
            countInc++;

            addPigletsToSeroincidenceCohort();

            if((countInc % sim.seroincidenceCohortsPer) == 0)
            {
                System.out.println("rrrrrrrrrrr Recording: now: " + now + " perIncTimer: " + perIncTimer + " countInc: " + countInc);
                //System.out.println ("Calculating seroincidence");
                calculateSeroincidencenPrint();
                calculatePerSeroPrevalencesnPrint();
                //System.out.println ("Updating seroincidence cohorts");
            }

            removePigsFromSeroincidenceCohort();

            if(countInc == seroIncNumSteps)
            {

                System.out.println("ererererererererere End of record: now: " + now + " perIncTimer: " + perIncTimer + " countInc: " + countInc);
                countInc = -100;
                perIncTimer = 1;

                sim.pigletsnPrint = sim.pigletsnPrint + seroIncs.get(0)/(double)sim.seroincidenceCohortsNumRounds;
                sim.pigsnPrint = sim.pigsnPrint + seroIncs.get(1)/(double)sim.seroincidenceCohortsNumRounds;
                sim.seroincnPrint = sim.seroincnPrint + seroIncs.get(2)/(double)sim.seroincidenceCohortsNumRounds;
                sim.seroincnPrintNorm++;
            }
        }



    }


    //====================================================
    public void calculateSeroincidencenPrint()
    {
        pigsSeroIncidenceSim = 0;
        pigsSeroIncidenceSimNorm = 0;

        int pigsCohortNorm = 0;
        int pigsCohort = 0;

        //pigletsSeroPrevalenceSim = 0.0;
        //pigletsSeroPrevalenceSimNorm = 0.0;

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(p.imported)continue;

            //if(pig.isAPigletSeroInc())
            //{
            //    pigletsSeroPrevalenceSimNorm++;
            //    if(p.seropositive)pigletsSeroPrevalenceSim++;

            //    if(p.seropositive)pigsSeroIncidenceSim++;
            //    pigsSeroIncidenceSimNorm++;
            //}


            if(!p.isAPiglet()
                    && p.isInTheSeroIncCohortSim)
            {
                pigsCohortNorm++;
                if(p.seropositive)pigsCohort++;
            }

            if(p.isInTheSeroIncCohortSim)
            {
                if(p.seropositive)pigsSeroIncidenceSim++;
                pigsSeroIncidenceSimNorm++;
            }
        }

        //System.out.println("piglets seropos: " + pigletsSeroPrevalenceSim);
        //System.out.println("----------------");
        //System.out.println("piglets norm: " + pigletsSeroPrevalenceSimNorm);
        //System.out.println("piglets num seropos: " + pigletsSeroPrevalenceSim);

        //------
        //System.out.println("------------------------------");
        //System.out.println("seroinc norm: " + pigsSeroIncidenceSimNorm);

        //System.out.println("seroinc num seropos: " + pigsSeroIncidenceSim);

        //System.out.println(" ");
        //System.out.println("pigsCohort norm: " + pigsCohortNorm);
        //System.out.println("pigsCohort num seropos: " + pigsCohort);
        //System.out.println("pigsCohort seroprev: " + (double)pigsCohort/(double)pigsCohortNorm);


        if(pigsSeroIncidenceSimNorm != 0)pigsSeroIncidenceSim = pigsSeroIncidenceSim/pigsSeroIncidenceSimNorm;
        else pigsSeroIncidenceSim = 0.0;

        seroIncs.set(2, (pigsSeroIncidenceSim + seroIncs.get(2)));

        //-----
        //System.out.println(" ");
        //System.out.println(" ");
        //System.out.println("seroincidence: " + pigsSeroIncidenceSim);

        //if(pigletsSeroPrevalenceSimNorm != 0)pigletsSeroPrevalenceSim = pigletsSeroPrevalenceSim/(double)pigletsSeroPrevalenceSimNorm;

        sim.seroIncidencePigsNorm++; 
        sim.seroIncidencePigs = sim.seroIncidencePigs + pigsSeroIncidenceSim; 

    }


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
 
            if(p.age> 523)
            {
                p.printResume();
                p.household.printResume();
            }

            if(p.isAPigletSeroInc())
            {
                if(p.seropositive)piglets++;
                pigletsNorm++;
            }
            else
            {
                if(p.seropositive)pigs++;
                pigsNorm++;
            }

            simPrevalenceByAgePigsNorm.set(p.age, (simPrevalenceByAgePigsNorm.get(p.age) + 1)); 
            if(p.seropositive)simPrevalenceByAgePigs.set(p.age, (simPrevalenceByAgePigs.get(p.age) + 1)); 

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

        double tmp = 0.0; 
        if(pigletsNorm != 0)tmp = piglets/pigletsNorm;
        seroIncs.set(0, (tmp + seroIncs.get(0)));

        tmp = 0.0; 
        if(pigsNorm != 0)tmp = pigs/pigsNorm;
        seroIncs.set(1, (tmp + seroIncs.get(1)));

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
    public void calculateSeroStats()
    {
        pigsSeroIncidenceSim = 0;
        pigsSeroIncidenceSimNorm = 0;

        pigletsSeroPrevalenceSim = 0.0;
        pigletsSeroPrevalenceSimNorm = 0.0;

        pigsSeroPrevalenceSim = 0.0;
        pigsSeroPrevalenceSimNorm = 0.0;

        int pigsCohortNorm = 0;
        int pigsCohort = 0;

        //pigletsSeroPrevalenceSim = 0.0;
        //pigletsSeroPrevalenceSimNorm = 0.0;

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(p.imported)continue;

            //if(pig.isAPigletSeroInc())
            //{
            //    pigletsSeroPrevalenceSimNorm++;
            //    if(p.seropositive)pigletsSeroPrevalenceSim++;

            //    if(p.seropositive)pigsSeroIncidenceSim++;
            //    pigsSeroIncidenceSimNorm++;
            //}

            //here it consider the seroprevalence of pigs only in the age 
            //group age > 4 months
            if(!p.isAPiglet())
            {
                pigsSeroPrevalenceSimNorm++;
                if(p.seropositive)pigsSeroPrevalenceSim++;
            }

            if(p.isAPigletSeroInc() && !p.imported)
            {
                pigletsSeroPrevalenceSimNorm++;
                if(p.seropositive)pigletsSeroPrevalenceSim++;
            }

            if(!p.isAPiglet()
                    && p.isInTheSeroIncCohortSim)
            {
                pigsCohortNorm++;
                if(p.seropositive)pigsCohort++;
            }

            if(p.isInTheSeroIncCohortSim)
            {
                if(p.seropositive)pigsSeroIncidenceSim++;
                pigsSeroIncidenceSimNorm++;
            }

        }

        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Statistics <<<<<<<<<<<<<<<<<<");
        //System.out.println("piglets seropos: " + pigletsSeroPrevalenceSim);
        //System.out.println("now: " + now);
        //System.out.println("piglets norm: " + pigletsSeroPrevalenceSimNorm);
        //System.out.println("piglets num seropos: " + pigletsSeroPrevalenceSim);

        //------
        //System.out.println("------------------------------");
        //System.out.println("seroinc norm: " + pigsSeroIncidenceSimNorm);
        //System.out.println("seroinc num seropos: " + pigsSeroIncidenceSim);

        //System.out.println(" ");
        //System.out.println("pigsCohort norm: " + pigsCohortNorm);
        //System.out.println("pigsCohort num seropos: " + pigsCohort);
        //System.out.println("pigsCohort seroprev: " + (double)pigsCohort/(double)pigsCohortNorm);

        if(pigletsSeroPrevalenceSimNorm != 0)pigletsSeroPrevalenceSim = pigletsSeroPrevalenceSim/(double)pigletsSeroPrevalenceSimNorm;

        if(pigsSeroPrevalenceSimNorm != 0)pigsSeroPrevalenceSim = pigsSeroPrevalenceSim/(double)pigsSeroPrevalenceSimNorm;

        if(pigsSeroIncidenceSimNorm != 0)pigsSeroIncidenceSim = pigsSeroIncidenceSim/pigsSeroIncidenceSimNorm;
        else pigsSeroIncidenceSim = 0.0;

        sim.seroIncidencePigs = sim.seroIncidencePigs + pigsSeroIncidenceSim; 
        sim.seroPrevalencePigs = sim.seroPrevalencePigs + pigsSeroPrevalenceSim;
        sim.seroPrevalencePiglets = sim.seroPrevalencePiglets + pigletsSeroPrevalenceSim;

        sim.seroPrevalencePigletsNorm++; 
        sim.seroPrevalencePigsNorm++; 
        sim.seroIncidencePigsNorm++; 
    }

    //====================================================
    public void addPigletsToSeroincidenceCohort()
    {
        //System.out.println(">>>>>>>> Adding piglets ");
        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(p.isAPigletSeroInc())
            {
                p.isInTheSeroIncCohortSim = true;
            }
        }
    }

    //====================================================
    public void removePigsFromSeroincidenceCohort()
    {
        //System.out.println(">>>>>>>> removing pigs ");
        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(!p.isAPiglet())
            {
                if(p.isInTheSeroIncCohortSim
                && p.seropositive)p.isInTheSeroIncCohortSim = false;
            }
        }
    }

    //====================================================
    public void updateSeroincidenceCohort()
    {
        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(p.isAPigletSeroInc())
            {
                p.isInTheSeroIncCohortSim = true;
            }
            if(!p.isAPiglet())
            {
                if(p.isInTheSeroIncCohortSim)
                {
                    if(p.seropositive)p.isInTheSeroIncCohortSim = false;
                }
            }
        }
    }

    //====================================================
    public void checkCysts(String word)
    {
        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(pig.numCysts < 0)System.out.println(word + " checkCysts id: " + pig.identity + " Atttttt---- pig.numCysts: " + pig.numCysts);
        }
    }

    //====================================================
    public void calcStats()
    {

        humansStats();

        //checkCysts("calcStats second");
        pigsStatsCysts();

        //System.out.println("prima");
        //checkCysts("calcStats third");
        addPigCystsHisto();

        addPigCystsHistoProg();

        householdsStats();

        calcHouseholdsSeroInRange();

        //calculate the cystiHuman stats
        if(sim.cystiHumans && !sim.burnin)sim.humanCH.cystiHumansStats(this);
    }

    //===============================================
    public  void addPigCystsHistoProg()
    {
        initHistoCystsProg();

        int stats = 0;
        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(pig.imported)continue;

            if(pig.numCysts == 0)
            {
                double tmp = pigCystsHistoProgLocal.get(0);
                tmp++;
                pigCystsHistoProgLocal.put(0, tmp);
            }
            else if(pig.numCysts > 0 && pig.numCysts <= 10)
            {
                double tmp = pigCystsHistoProgLocal.get(10);
                tmp++;
                pigCystsHistoProgLocal.put(10, tmp);
            }
            else if(pig.numCysts > 10 && pig.numCysts <= 100)
            {
                double tmp = pigCystsHistoProgLocal.get(100);
                tmp++;
                pigCystsHistoProgLocal.put(100, tmp);
            }
            else if(pig.numCysts > 100 && pig.numCysts <= 1000)
            {
                double tmp = pigCystsHistoProgLocal.get(1000);
                tmp++;
                pigCystsHistoProgLocal.put(1000, tmp);
            }
            else if(pig.numCysts > 1000 && pig.numCysts <= 10000)
            {
                double tmp = pigCystsHistoProgLocal.get(10000);
                tmp++;
                pigCystsHistoProgLocal.put(10000, tmp);
            }
            else if(pig.numCysts > 10000 && pig.numCysts <= 100000)
            {
                double tmp = pigCystsHistoProgLocal.get(100000);
                tmp++;
                pigCystsHistoProgLocal.put(100000, tmp);
            }
            else if(pig.numCysts > 100000)
            {
                double tmp = pigCystsHistoProgLocal.get(1000000);
                tmp++;
                pigCystsHistoProgLocal.put(1000000, tmp);
            }
        }

        //double d = (double)(sim.pigsBag.size() - pigCystsHistoProgLocal.get(0))/(double)sim.pigsBag.size();
        //System.out.println("Histo1: " + pigCystsHistoProgLocal.get(0));


        //normPigHistoProg();


    }



    //===============================================
    public  void addPigCystsHisto()
    {
        initHistoCysts();

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig pig = (Pig)sim.pigsBag.get(i);

            if(pig.imported)continue;

            int nB = (int)Math.ceil((double)pig.numCysts/(double)sim.extensionBinsPigCystsHisto);
            nB = (int)((double)nB * (double)sim.extensionBinsPigCystsHisto);
            //System.out.println("---- nB: " + nB);
            //System.out.println("---- pig.numCysts: " + pig.numCysts);

            if(pig.numCysts < 0)
            {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(sim.villageName + " Att!!");
                System.out.println("---- pig.numCysts: " + pig.numCysts);
                pig.printResume();
                System.exit(0);

            }

            //if(nB == 0 && pig.numCysts != 0)
            //{
            //    System.out.println("Att!!");
            //    System.out.println("---- nB: " + nB);
            //    System.out.println("---- pig.numCysts: " + pig.numCysts);
            //
            //}

            if(pig.numCysts == 0)
            {
                double tmp = pigCystsHistoLocal.get(0);
                tmp++;
                pigCystsHistoLocal.put(0, tmp);
            }
            else if(nB >= sim.numBinsPigCystsHisto * sim.extensionBinsPigCystsHisto)
            {
                //System.out.println("---- Pig number of cyst at slaughter: " + pig.numCysts);
                //System.out.println("---- this number exceeds the max number of cysts in pig cysts histogram");
                double tmp = pigCystsHistoLocal.get(sim.numBinsPigCystsHisto * sim.extensionBinsPigCystsHisto);
                tmp++;
                pigCystsHistoLocal.put((sim.numBinsPigCystsHisto * sim.extensionBinsPigCystsHisto), tmp);
            }
            else
            {
                //System.out.println(nB);
                double tmp = pigCystsHistoLocal.get(nB);
                tmp++;
                pigCystsHistoLocal.put(nB, tmp);
            }
        }

        //normPigHisto();
    }



    //====================================================
    public void writeXlsOutput()
    {
        if(sim.newPigCysts)
        {
            //Writes the weekly statistics --------------------------
            Object[] obj = new Object[] {
                (double)now,//step
                    //(double)now/sim.hoursInAWeek,//week
                    (double)sim.numWeeks,
                    //Human outputs -------------------
                    (double)humansInTheVillage,
                    (double)humanTaeniasisPrev,
                    //pig   outputs -------------------
                    (double)sim.pigsBag.size(),
                    (double)pigInfectedPrevCysts,
                    (double)pigsSeroPrevalenceSim,
                    (double)pigletsSeroPrevalenceSim,
                    (double)pigsSeroIncidenceSim,
                    (double)sim.numContaminatedSites,
                    (double)avgNumCystsFromProglottidsNoNecro,
                    (double)avgNumCystsFromEggsNoNecro,
                    (double)avgNumCystsNoNecro
            };

            if(sim.doInterventions && 1 != 1)
            {
                int len = obj.length + 7;
                Object[] obj2 = new Object[len];

                len = obj.length;
                for (int i = 0; i < len; i++)
                {
                    obj2[i] = obj[i];
                }

                //String tmp = "";

                //System.out.println(sim.villageName + " roundSero: " + sim.roundSero);
                //-------------------
                if(sim.interventionStep == 1)obj2[len] = (double)(sim.interventionStep);
                else obj2[len] = (double)-0.01;

                //-------------------
                if(sim.serosampleDone == 1) obj2[len + 1] = (double)(sim.interSeroPrevalencePigsRounds.get(sim.roundSero));
                else obj2[len + 1] = (double)-0.01;
                //-------------------
                if(sim.serosampleDone == 1)obj2[len + 2] = (double)(sim.obsSeroPrevalencePigsRounds.get(sim.roundSero));
                else obj2[len + 2] = (double)-0.01;

                //-------------------
                if(sim.serosampleDone == 1) obj2[len + 3] = (double)(sim.interSeroPrevalencePigletsRounds.get(sim.roundSero));
                else obj2[len + 3] = (double)-0.01;
                //-------------------
                if(sim.serosampleDone == 1)obj2[len + 4] = (double)(sim.obsSeroPrevalencePigletsRounds.get(sim.roundSero));
                else obj2[len + 4] = (double)-0.01;

                //-------------------
                if(sim.serosampleDone == 1) obj2[len + 5] = (double)(sim.interSeroIncidencePigsRounds.get(sim.roundSero));
                else obj2[len + 5] = (double)-0.01;
                //-------------------
                if(sim.serosampleDone == 1) obj2[len + 6] = (double)(sim.obsSeroIncidencePigsRounds.get(sim.roundSero));
                else obj2[len + 6] = (double)-0.01;

                //-------------------
                obj2[len + 7] = (int)(sim.pigsBag.size());

                obj = obj2;

            }

            if(sim.pigsImmunity)
            {
                int len = obj.length + 5;
                Object[] obj2 = new Object[len];

                len = obj.length;
                for (int i = 0; i < len; i++)
                {
                    obj2[i] = obj[i];
                }

                obj2[len] = (double) avgImmunityO;
                obj2[len + 1] = (double) avgImmunityI;
                obj2[len + 2] = (double) avgImmunityC;
                obj2[len + 3] = (double) avgDegeneratedCystsINoNecro;
                obj2[len + 4] = (double) avgDegeneratedCystsCNoNecro;

                obj = obj2;
            }

            if(sim.cystiHumans)
            {
                int len = obj.length + 53; // GB16nars
                Object[] obj2 = new Object[len];

                len = obj.length;
                for (int i = 0; i < len; i++)
                {
                    obj2[i] = obj[i];
                }

                obj2[len] = (double)NCCPrevalence;
                obj2[len + 1] = (double)humanNCCShareByNumberOfLesion.get(0);
                obj2[len + 2] = (double)humanNCCShareByNumberOfLesion.get(1);
                obj2[len + 3] = (double)humanNCCShareByNumberOfLesion.get(2);
                obj2[len + 4] = (double)humanNCCShareByNumberOfLesion.get(3);
                obj2[len + 5] = (double)humanNCCShareByNumberOfLesion.get(4);

                obj2[len + 6] = (double)humanNCCPrevByAge.get(0);
                obj2[len + 7] = (double)humanNCCPrevByAge.get(1);
                obj2[len + 8] = (double)humanNCCPrevByAge.get(2);
                obj2[len + 9] = (double)humanNCCPrevByAge.get(3);
                obj2[len + 10] = (double)humanNCCPrevByAge.get(4);
                obj2[len + 11] = (double)humanNCCPrevByAge.get(5);
                obj2[len + 12] = (double)humanNCCPrevByAge.get(6);

                obj2[len + 13] = (double)NCCRelatedEpiPrevalence;
                obj2[len + 14] = (double)NCCRelatedAEpiPrevalence;
                obj2[len + 15] = (double)NCCRelatedICHPrevalence;

                obj2[len + 16] = (double)shareNCCcasesNonCalcified;
                obj2[len + 17] = (double)shareofExParinNCCcases;
                obj2[len + 18] = (double)shareofNCCcaseswithEpi;
                obj2[len + 19] = (double)shareofParenchymalinICH;
                obj2[len + 20] = (double)shareofEpiNCCcalcifiedWithActiveEpi;
                obj2[len + 21] = (double)shareofAEcasesThatAreNonCalcified;
                obj2[len + 22] = (double)shareofEpiNCCcalcifiedWithActiveEpiMoyano;
                obj2[len + 23] = (double)shareofAEcasesThatAreNonCalcifiedMoyano;
                obj2[len + 24] = (double)shareofAEcasesUnderTreatment;

                obj2[len + 25] = (double)sim.nbDeathICH;
                obj2[len + 26] = (double)sim.nbDeathEpi;
                obj2[len + 27] = (double)sim.nbDeathNatural;
                obj2[len + 28] = (double)sim.nbICHSurgeries;

              //  obj2[len + 30] = (double)sim.newICHoverNewAE; // GB keep commented for potential future use
              //  obj2[len + 31] = (double)sim.ichOverAE; // GB keep commented for potential future use

              //  obj2[len + 30] = (double)NCCRelatedEpiPrevalenceEmigrants;
              //  obj2[len + 31] = (double)NCCRelatedICHPrevalenceEmigrants;
              //  obj2[len + 32] = (double)shareofAEcasesUnderTreatmentEmigrants;
                obj2[len + 29] = (double)sim.nbAEWeeksB; // GB19mai
                obj2[len + 30] = (double)sim.nbTreatedAEWeeksB; // GB19mai
                obj2[len + 31] = (double)sim.nbICHWeeksB; // GB19mai
              //  obj2[len + 32] = (double)sim.agesIncidentICHcases; // GB19mai
              //  obj2[len + 33] = (double)sim.agesIncidentAEcases; // GB19mai
              //  obj2[len + 34] = (double)nICHCases; // GB19mai
              //  obj2[len + 35] = (double)nICHCasesEmigrants; // GB19mai
              //  obj2[len + 36] = (double)nParInICH; // GB19mai
                obj2[len + 32] = (double)numHumansTraveling;
                obj2[len + 33] = (double)numHumansStrangerTravelers;
                obj2[len + 34] = (double)sim.emigrantsBag.size();
                //obj2[len + 40] = (double)sim.nbIncidentAEcases; // GB19mai
                //obj2[len + 41] = (double)sim.nbIncidentICHcases; // GB19mai
                //obj2[len + 42] = (double)sim.incidentParICHCyst; // GB19mai
                //obj2[len + 43] = (double)sim.incidentExParICHCyst; // GB19mai
                //obj2[len + 44] = (double)sim.incidentVisibleCystPar; // GB19mai
                //obj2[len + 45] = (double)sim.incidentVisibleCystEP; // GB19mai
                obj2[len + 35] = (double)humanByAge.get(0);
                obj2[len + 36] = (double)humanByAge.get(1);
                obj2[len + 37] = (double)humanByAge.get(2);
                obj2[len + 38] = (double)humanByAge.get(3);
                obj2[len + 39] = (double)humanByAge.get(4);
                obj2[len + 40] = (double)humanByAge.get(5);
                obj2[len + 41] = (double)humanByAge.get(6);
                obj2[len + 42] = (double)humanByAge.get(7);
                obj2[len + 43] = (double)shareEverNCC; // GBPIAE
                obj2[len + 44] = (double)shareEverNCC60; // GBPIAE
              //  obj2[len + 56] = (double)nActiveLesionCases; // GB19mai
              //  obj2[len + 57] = (double)nAECaseswithNonCalcified; // GB19mai
              //  obj2[len + 58] = (double)nEpiCasesNonCalcified; // GB19mai
              //  obj2[len + 59] = (double)nEpiCasesVisible; // GB19mai
              //  obj2[len + 60] = (double)nActiveEpiCasesVisible; // GB19mai
            //    obj2[len + 64] = (double)shareEverNCCdisa; // GBPIAE
              //  obj2[len + 61] = (double)nIAECalcified; // GB19mai
              //  obj2[len + 66] = (double)nIAECalcifiedEverdisa; // GBPIAE
                obj2[len + 45] = (double)shareofNCCcaseswithAE; // GBPIAE
              //  obj2[len + 46] = ((double)sim.nbAEWeeksEmigrants) * (double)sim.nPrint; // GB19mai
              //  obj2[len + 47] = ((double)sim.nbTreatedAEWeeksEmigrants) * (double)sim.nPrint; // GB19mai
              //  obj2[len + 48] = ((double)sim.nbICHWeeksEmigrants)* (double)sim.nPrint; // GB19mai
                obj2[len + 46] = (double)shareofEpiNCCcalcifiedWithActiveEpiCutOff;
                obj2[len + 47] = (double)shareWithViableinNonCalcified; // GB26avril
                obj2[len + 48] = (double)shareWithDegeneratedinNonCalcified; // GB26avril
            //    obj2[len + 72] = (double)shareAEinNonCalcified; // GB16mars
            //    obj2[len + 73] = (double)sim.shareEverTaenia; // GB17mars
            //    obj2[len + 74] = (double)sim.shareEverTaenia60; // GB17mars
                obj2[len + 49] = (double)share1CystInAdultNCC; // GBRicaPlaya
                obj2[len + 50] = (double)share11ormoreCystsInAdultNCC; // GBRicaPlaya
                obj2[len + 51] = (double)epiPrevalenceinAdultNCCCases; // GBRicaPlaya
                obj2[len + 52] = (double)adultNCCPevalenceCT; // GBRicaPlaya


                obj = obj2;


            }

            sim.weeklyData.add(obj);

            if(sim.extendedOutput)System.out.println("write weeklyData size: " + sim.weeklyData.size());
        }
        else
        {
            //Writes the weekly statistics --------------------------
            sim.weeklyData.add(new Object[] {
                (double)now,//step
                    //(double)now/sim.hoursInAWeek,//week
                    (double)sim.numWeeks,
                    //Human outputs -------------------
                    (double)humansInTheVillage,
                    (double)humanTaeniasisPrev,
                    //pig   outputs -------------------
                    (double)sim.pigsBag.size(),
                    (double)pigInfectedPrev,
                    (double)pigHighlyInfectedPrev,
                    (double)pigLightlyInfectedPrev,
                    (double)pigsSeroPrevalenceSim,
                    (double)pigletsSeroPrevalenceSim,
                    (double)pigsSeroIncidenceSim,
                    (double)sim.numContaminatedSites,
            });
        }


        //System.out.println ("Statistics stops");
    }


    //====================================================
    public void writeStandardOutput()
    {
        System.out.println("=================================================");
        System.out.println(sim.villageName + " Step: " +  now);
        System.out.println(sim.villageName + " Week: " +  sim.numWeeks);

        if(sim.burnin)
        {
            System.out.println(sim.villageName + " --------------------------------------- ");
            System.out.println (sim.villageName + " Burn-in: " + sim.burninPeriodCounter);
            System.out.println(" ");

        }
        System.out.println(sim.villageName + " Household stats ------------------------ ");
        System.out.println(sim.villageName + " Num Households: " + sim.householdsBag.size());
        System.out.println(sim.villageName + " Num Households with latrine: " + numHouseholdWithLatrine);
        //System.out.println(sim.villageName + " Max num carnes meal in a sigle household in a week: " + sim.maxNumCarnesWeekHousehold);
        //System.out.println(sim.villageName + " Max num carnes meal in a sigle household in a week per person: " + sim.maxNumCarnesWeekHouseholdPerson);

        System.out.println(sim.villageName + " Humans stats ------------------------ ");
        System.out.println(sim.villageName + " Num Humans in the village: " + humansInTheVillage);
        System.out.println(sim.villageName + " Human taeniasis prevalence: " + humanTaeniasisPrev);
        System.out.println(sim.villageName + " Avg. Human taeniasis prevalence: " + sim.infectedHumansPrevalence/sim.numWeeksPrint);
        System.out.println(sim.villageName + " Num Infected Humans: " + numInfectedHumans);

        if(sim.travelerProp > 0.0)System.out.println(sim.villageName + " Num humans traveling: " + numHumansTraveling);
        if(sim.travelerProp > 0.0 && sim.strangerTraveler)
        {
            System.out.println(sim.villageName + " Num stranger travelers: " + numHumansStrangerTravelers);
            System.out.println(sim.villageName + " Num infected stranger travelers: " + numHumansStrangerTravelersInfected);

            if(sim.strangerTravelersBag.size() != 0)System.out.println(sim.villageName + " Stranger travelers prevalence: " + (double)numHumansStrangerTravelersInfected/(double)sim.strangerTravelersBag.size());

            System.out.println(sim.villageName + " Avg. Stranger travelers prevalence: " + sim.prevHumansStrangerTravelers/sim.numWeeksPrint);
        }

        if(sim.cystiHumans)
        {
            System.out.println(sim.villageName + " Cysti Humans stats ---------------------- ");
            System.out.println(sim.villageName + " Number of human cysts, including immature & disappeared cysts: " + sim.humanCystsBag.size()); // GBPIAE

            System.out.println(sim.villageName + " Human cysts per human, , including immature & disappeared cysts: "
            + (double)sim.humanCystsBag.size()/(double)(sim.humansBag.size())); // GBPIAE
        }


        System.out.println(sim.villageName + " Pigs stats ---------------------- ");
        System.out.println(sim.villageName + " Number of pigs in this time step: " + sim.pigsBag.size());
        System.out.println(sim.villageName + " Pigs cysticercosis infection Prevalence: " + pigInfectedPrevCysts);
        System.out.println(sim.villageName + " Avg. pig cysticercosis infection Prevalence: " + sim.infectedPigsPrevalenceCysts/sim.numWeeksPrint);
        System.out.println(sim.villageName + " Num cysts per pig: " + avgNumCystsNoNecro);
        System.out.println(sim.villageName + " Avg. Num cysts per pig: " + sim.avgNumCystsNoNecro/sim.numWeeksPrint);
        System.out.println(sim.villageName + " Num cysts from proglottids per pig: " + avgNumCystsFromProglottidsNoNecro);
        System.out.println(sim.villageName + " Avg. Num cysts from proglottids per pig: " + sim.avgNumCystsFromProglottidsNoNecro/sim.numWeeksPrint);
        System.out.println(sim.villageName + " Num cysts from eggs per pig: " + sim.avgNumCystsFromEggsNoNecro);
        System.out.println(sim.villageName + " Avg. Num cysts from eggs per pig: " + sim.avgNumCystsFromEggsNoNecro/sim.numWeeksPrint);

        if(sim.pigsImmunity)
        {
            System.out.println(sim.villageName + " Pigs immunity ------------------- ");
            System.out.println(sim.villageName + " Pigs average immunity against cysts (immunityC): " + sim.avgImmunityC/sim.numWeeksPrint);
            System.out.println(sim.villageName + " Pigs average immunity against oncospheres (immunityO): " + sim.avgImmunityO/sim.numWeeksPrint);
            System.out.println(sim.villageName + " Pigs average immunity against cysts immature stage (immunityI): " + sim.avgImmunityI/sim.numWeeksPrint);
            System.out.println(sim.villageName + " Pigs average number of degenerated mature cysts per pig: " + sim.avgDegeneratedCystsC/sim.numWeeksPrint);
            System.out.println(sim.villageName + " Pigs average numbe of degenerated immature cysts per pig: " + sim.avgDegeneratedCystsI/sim.numWeeksPrint);

        }
        //System.out.println("defec created: " + sim.statsDebug);
        //System.out.println("defec died: " + sim.statsDebug1);

        //System.out.println(sim.villageName + " Avg. light cyst. inf. Prev. for pigs with a tapeworm carrier in home-range: " + sim.lightInfectedPigsPrevalenceCloseTapeworm / sim.numWeeksPrint);
        //System.out.println(sim.villageName + " Avg. light cyst. inf. Prev. for pigs with no tapeworm carrier in home-range: " + sim.lightInfectedPigsPrevalenceNoCloseTapeworm / sim.numWeeksPrint);

        //System.out.println(sim.villageName + " Avg. fract. pigs with tapeworm carrier in home-range: " + sim.avgFractPigsCloseTapeworm / sim.numWeeksPrint);
        System.out.println(sim.villageName + " Pigs serology data --------------------------");

        String tmp = " Piglets Seroprevalence (" + sim.cohortSeroIncAgesInMonths.get(0) + " < age < " +  sim.cohortSeroIncAgesInMonths.get(1) +  " months): ";
        System.out.println(sim.villageName + tmp + pigletsSeroPrevalenceSim);
        tmp = " Avg. Piglets Seroprevalence (" + sim.cohortSeroIncAgesInMonths.get(0) + " < age < " +  sim.cohortSeroIncAgesInMonths.get(1) +  " months): ";
        System.out.println(sim.villageName + tmp +  sim.seroPrevalencePiglets/(double)sim.seroPrevalencePigletsNorm);

        System.out.println(sim.villageName + " Pigs seroprevalence (age > " + sim.cohortSeroIncAgesInMonths.get(1)  + " months): " + pigsSeroPrevalenceSim);
        System.out.println(sim.villageName + " Avg. Pigs seroprevalence (age > " + sim.cohortSeroIncAgesInMonths.get(1)  + " months): " + sim.seroPrevalencePigs/(double)sim.seroPrevalencePigsNorm);

        System.out.println("--------------------------");
        System.out.println(sim.villageName + " Pigs seroincidence: " + pigsSeroIncidenceSim);
        tmp = " Avg. pigs seroincidence ";
        System.out.println(sim.villageName + tmp +  sim.seroIncidencePigs/(double)sim.seroIncidencePigsNorm);

        //---
        System.out.println("--------------------------");
        tmp = " Avg. Piglets Seroprevalence with the seroprevalence cohorts periodicity (7 <= age <= 14 weeks): ";
        System.out.println(sim.villageName + tmp +  sim.perseroPrevalencePiglets/(double)sim.perseroPrevalencePigletsNorm);

        tmp = " Avg. Pigs Seroprevalence in the transition age segment with the seroincidence cohorts periodicity (15 <= age <= 20): ";
        System.out.println(sim.villageName + tmp +  sim.perseroPrevalenceAgeTransition/(double)sim.perseroPrevalenceAgeTransitionNorm);

        tmp = " Avg. Young Pigs Seroprevalence with the seroprevalence cohorts periodicity (21 <= age <= 37 weeks): ";
        System.out.println(sim.villageName + tmp +  sim.perseroPrevalencePigsYoung/(double)sim.perseroPrevalencePigsYoungNorm);

        tmp = " Avg. Adult Pigs Seroprevalence with the seroprevalence cohorts periodicity (38 <= age weeks): ";
        System.out.println(sim.villageName + tmp +  sim.perseroPrevalencePigsAdult/(double)sim.perseroPrevalencePigsAdultNorm);

        //System.out.println("--------------------------");
        //System.out.println("New periodic and nPrint seroInc stats");

        tmp = "Avg. Piglets Seroprevalence with cohorts periodicity and nPrint (" + sim.cohortSeroIncAgesInMonths.get(0) + " < age < " +  sim.cohortSeroIncAgesInMonths.get(1) +  " months): ";
        System.out.println(sim.villageName + tmp + sim.pigletsnPrint/sim.seroincnPrintNorm);

        tmp = sim.villageName + " Avg. Pigs seroprevalence with cohorts periodicity and nPrint (age > " + sim.cohortSeroIncAgesInMonths.get(1)  + " months): ";
        System.out.println(tmp + sim.pigsnPrint/(double)sim.seroincnPrintNorm);

        tmp = sim.villageName + " Avg. seroincidence with cohorts periodicity and nPrint: ";
        System.out.println(tmp + sim.seroincnPrint/(double)sim.seroincnPrintNorm);

        System.out.println("");
        System.out.println("--------------------------");
        tmp = " Avg. pigs seroprevalence in range for households in the border: ";
        System.out.println(sim.villageName + tmp +  sim.seroPrevHousesInTheBorder/sim.seroPrevHousesInTheBorderNorm);
        tmp = " Avg. pigs seroprevalence in range for households NOT in the border: ";
        System.out.println(sim.villageName + tmp +  sim.seroPrevHousesNotInTheBorder/sim.seroPrevHousesNotInTheBorderNorm);
        tmp = " Perc. increase of avg. pigs seroprevalence in range for households in the border: ";
        double dd = (sim.seroPrevHousesInTheBorder/sim.seroPrevHousesInTheBorderNorm) - (sim.seroPrevHousesNotInTheBorder/sim.seroPrevHousesNotInTheBorderNorm);
        dd = dd / (sim.seroPrevHousesNotInTheBorder/sim.seroPrevHousesNotInTheBorderNorm);
        dd = dd * 100;
        System.out.println(sim.villageName + tmp +  dd);
        System.out.println("--------------------------");

        //---
        System.out.println(sim.villageName + " Defec. Sites stats ---------------------- ");
        System.out.println(sim.villageName + " Number of contaminated defec. sites: " + sim.numContaminatedSites);
        System.out.println(sim.villageName + " Number defec. sites contaminated with eggs: " + sim.eggs);
        System.out.println(sim.villageName + " Number of defec. sites contaminated with proglottids: " + sim.proglottid);

        System.out.println(sim.villageName + " Meat Portions Stats -------------------- ");
        System.out.println(sim.villageName + " Proportion of pig meat consumed by onwners: " + (double)sim.numMeatPortionsConsumedByOwners/(double)sim.totNumMeatPortions);
        System.out.println(sim.villageName + " Proportion of pig meat distributed to village households (not including the owner): " + (double)sim.numMeatPortionsDistributedToHouseholds/(double)sim.totNumMeatPortions);
        System.out.println(sim.villageName + " Proportion of pig meat portions exported: " + (double)sim.numMeatPortionsSold/(double)sim.totNumMeatPortions);

        System.out.println (sim.villageName + " step stats end  -----------");
        System.out.println("=================================================");

    }

    //====================================================
    public void householdsStats()
    {
        numHousehold = 0.0;
        numHouseholdWithLatrine = 0.0;

        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household h = (Household)sim.householdsBag.get(i);

            if(h.latrine)
            {
                numHouseholdWithLatrine++;
            }
        }
    }

    //====================================================
    public void humansStats()
    {
        humanTaeniasisPrev = 0.0;
        humansInTheVillage = 0;
        numHumansTraveling = 0;
        numHumansStrangerTravelers = 0;
        numHumansStrangerTravelersInfected = 0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            //h.printResume();

            if(h.traveling)numHumansTraveling++;

            if(h.tapewormMature && !h.traveling && !h.strangerTraveler)
            {
                humanTaeniasisPrev++;
            }

            if(!h.traveling)humansInTheVillage++;

            if(h.strangerTraveler)numHumansStrangerTravelers++;
            if(h.strangerTraveler && h.tapewormMature)numHumansStrangerTravelersInfected++;

            simPrevalenceByAgeHumansNorm.set(h.age, (simPrevalenceByAgeHumansNorm.get(h.age) + 1)); 
            if(h.tapewormMature)simPrevalenceByAgeHumans.set(h.age, (simPrevalenceByAgeHumans.get(h.age) + 1)); 
        }

        numInfectedHumans = humanTaeniasisPrev;
        humanTaeniasisPrev = humanTaeniasisPrev/(double)sim.humansBag.size();
    }

    //====================================================
    public void pigsStatsCysts()
    {
        pigHighlyInfectedPrev = 0.0;
        pigLightlyInfectedPrev = 0.0;
        pigInfectedPrev = 0.0;
        pigInfectedPrevCysts = 0.0;

        avgImmunityC = 0.0;
        avgImmunityO = 0.0;
        avgImmunityI = 0.0;

        avgNumCystsNoNecro = 0.0;
        avgDegeneratedCystsCNoNecro = 0.0;
        avgDegeneratedCystsINoNecro = 0.0;
        avgNumCystsFromProglottidsNoNecro = 0.0;
        avgNumCystsFromEggsNoNecro = 0.0;


        //int numBreedingSow = 0;

        /*
        pigHighlyInfectedPrevCloseTapeworm = 0.0;
        pigHighlyInfectedPrevNoCloseTapeworm = 0.0;
        statsPigHighlyInfectedPrevNoCloseTapeworm = 0.0;
        statsPigHighlyInfectedPrevCloseTapeworm = 0.0;

        pigLightlyInfectedPrevCloseTapeworm = 0.0;
        pigLightlyInfectedPrevNoCloseTapeworm = 0.0;
        statsPigLightlyInfectedPrevNoCloseTapeworm = 0.0;
        statsPigLightlyInfectedPrevCloseTapeworm = 0.0;
        */

        int statsAge = 0;

        for(int i = 0; i < sim.pigsBag.size(); i++)
        {
            Pig p = (Pig)sim.pigsBag.get(i);

            if(p.imported)continue;

            //if(p.age > 1000)statsAge++;

            //System.out.println ("pig numCysts:" + p.numCysts);

            if(p.numCysts > 0)pigInfectedPrevCysts++;

            avgImmunityC = avgImmunityC + p.immunityC;
            avgImmunityO = avgImmunityO + p.immunityO;
            if(sim.immatureCystsPeriod > 0)avgImmunityI = avgImmunityI + p.immunityI.get(0);

            avgNumCystsNoNecro = avgNumCystsNoNecro + p.numCysts;
            avgDegeneratedCystsCNoNecro = avgDegeneratedCystsCNoNecro + p.numDegeneratedCystsIc;
            avgDegeneratedCystsINoNecro = avgDegeneratedCystsINoNecro + p.numDegeneratedCystsIi;
            avgNumCystsFromProglottidsNoNecro = avgNumCystsFromProglottidsNoNecro + p.numCystsFromProglottids;
            avgNumCystsFromEggsNoNecro = avgNumCystsFromEggsNoNecro + p.numCystsFromEggs;

            //if(p.breedingSow)numBreedingSow++;
        }

        //System.out.println ("Num of breeding sow: " + numBreedingSow);

        numHighlyInfectedPigs = pigHighlyInfectedPrev;
        numLightlyInfectedPigs = pigLightlyInfectedPrev;

        pigInfectedPrev = (pigLightlyInfectedPrev + pigHighlyInfectedPrev)/(double)sim.pigsBag.size();

        //System.out.println("-------------------------");
        //System.out.println("prev: " + (sim.pigsBag.size() - pigInfectedPrevCysts));

        pigInfectedPrevCysts = pigInfectedPrevCysts/(double)sim.pigsBag.size();

        //System.out.println("prev1: " + (1 - pigInfectedPrevCysts));
        //System.out.println("num pigs: " + sim.pigsBag.size());

        pigHighlyInfectedPrev = pigHighlyInfectedPrev/(double)sim.pigsBag.size();
        pigLightlyInfectedPrev = pigLightlyInfectedPrev/(double)sim.pigsBag.size();

        avgImmunityC = avgImmunityC / (double)sim.pigsBag.size();
        avgImmunityO = avgImmunityO / (double)sim.pigsBag.size();
        avgImmunityI = avgImmunityI / (double)sim.pigsBag.size();

        avgNumCystsNoNecro = avgNumCystsNoNecro/(double)sim.pigsBag.size();
        avgDegeneratedCystsCNoNecro = avgDegeneratedCystsCNoNecro/(double)sim.pigsBag.size();
        avgDegeneratedCystsINoNecro = avgDegeneratedCystsINoNecro/(double)sim.pigsBag.size();
        avgNumCystsFromProglottidsNoNecro = avgNumCystsFromProglottidsNoNecro/(double)sim.pigsBag.size();
        avgNumCystsFromEggsNoNecro = avgNumCystsFromEggsNoNecro/(double)sim.pigsBag.size();

        //System.out.println ("Pig statsAge: " + statsAge);

        /*
        if(statsPigHighlyInfectedPrevCloseTapeworm != 0)
        {
            pigHighlyInfectedPrevCloseTapeworm = pigHighlyInfectedPrevClosTapeworm/statsPigHighlyInfectedPrevCloseTapeworm;
            pigLightlyInfectedPrevCloseTapeworm = pigLightlyInfectedPrevCloseTapeworm/statsPigLightlyInfectedPrevCloseTapeworm;
        }
        else
        {
            pigHighlyInfectedPrevCloseTapeworm = 0.0;
            pigLightlyInfectedPrevCloseTapeworm = 0.0;
        }

        if(statsPigHighlyInfectedPrevNoCloseTapeworm != 0)
        {
            pigHighlyInfectedPrevNoCloseTapeworm = pigHighlyInfectedPrevNoCloseTapeworm/statsPigHighlyInfectedPrevNoCloseTapeworm;
            pigLightlyInfectedPrevNoCloseTapeworm = pigLightlyInfectedPrevNoCloseTapeworm/statsPigLightlyInfectedPrevNoCloseTapeworm;
        }
        else
        {
            pigHighlyInfectedPrevNoCloseTapeworm = 0.0;
            pigLightlyInfectedPrevNoCloseTapeworm = 0.0;
        }

        fractPigsCloseTapeworm = statsPigLightlyInfectedPrevCloseTapeworm / (double)sim.pigsBag.size();
        */

    }


    //====================================================
    public void initXlsData()
    {
        if(sim.newPigCysts)
        {
            Object[] obj = new Object[] {

                "Step",
                    "Week",
                    //Humans Statisitcs -----------
                    "Humans in the village",
                    "Human Taeniasis Prevalence",

                    //Pigs statistics
                    "Pigs in the village",
                    "Pigs Cysticercosis Infection Prevalence",
                    "Pigs Seroprevalence (age > " + sim.cohortSeroIncAgesInMonths.get(0) + " months)",
                    "Piglets Seroprevalence (" + sim.cohortSeroIncAgesInMonths.get(0) + " < age < " +  sim.cohortSeroIncAgesInMonths.get(1) +  " )",
                    "Pigs Seroincidence (" + sim.seroincidenceCohortsPerWrite + " months interval)",
                    "Num contaminated defec. sites",
                    "Avg. number of cysts from proglottids per pig",
                    "Avg. number of cysts from eggs per pig",
                    "Avg. number of cysts per pig",

            };

            if(sim.doInterventions && 1 != 1)
            {
                int len = obj.length + 7;
                Object[] obj2 = new Object[len];

                len = obj.length;
                for (int i = 0; i < len; i++)
                {
                    obj2[i] = obj[i];
                }

                obj2[len] = "Intervention";
                obj2[len + 1] = "Pigs sim. seroprevalence";
                obj2[len + 2] = "Pigs obs. seroprevalence";

                obj2[len + 3] = "Piglets sim. seroprevalence (2 < age <= 4 months)";
                obj2[len + 4] = "Piglets obs. seroprevalence (2 < age <= 4 months)";

                obj2[len + 5] = "Pigs sim. seroincidence (pigs in the cohort)";
                obj2[len + 6] = "Pigs obs. seroincidence (pigs in the cohort)";

                obj2[len + 7] = "Num. of Pigs";

                obj = obj2;
            }


            if(sim.pigsImmunity)
            {
                int len = obj.length + 5;
                Object[] obj2 = new Object[len];

                len = obj.length;
                for (int i = 0; i < len; i++)
                {
                    obj2[i] = obj[i];
                }

                obj2[len] = "Avg. immunityO";
                obj2[len + 1] = "Avg. immunityI";
                obj2[len + 2] = "Avg. immunityC";
                obj2[len + 3] = "Avg. number of immature deg. cysts per pig";
                obj2[len + 4] = "Avg. number of mature deg. cysts per pig";

                obj = obj2;
            }

            if(sim.cystiHumans)
            {
                int len = obj.length + 53; // GB16mars
                Object[] obj2 = new Object[len];

                len = obj.length;
                for (int i = 0; i < len; i++)
                {
                    obj2[i] = obj[i];
                }

                obj2[len] = "NCC prevalence in village";

                obj2[len + 1] = "% of cases with 1 cyst among NCC cases in the village";
                obj2[len + 2] = "% of cases with 2 cysts among NCC cases in the village";
                obj2[len + 3] = "% of cases with 3 cysts among NCC cases in the village";
                obj2[len + 4] = "% of cases with 4 cysts among NCC cases in the village";
                obj2[len + 5] = "% of cases with 5 or more cysts among NCC cases in the village";

                obj2[len + 6] = "NCC prevalence under 12 years (village)";
                obj2[len + 7] = "NCC prevalence between 12 and 19 years (village)";
                obj2[len + 8] = "NCC prevalence between 20 and 29 years (village)";
                obj2[len + 9] = "NCC prevalence between 30 and 39 years (village)";
                obj2[len + 10] = "NCC prevalence between 40 and 49 years (village)";
                obj2[len + 11] = "NCC prevalence between 50 and 59 years (village)";
                obj2[len + 12] = "NCC prevalence above 60 years (village)";

                obj2[len + 13] = "NCC related epilepsy prevalence (village)";
                obj2[len + 14] = "NCC related active epilepsy prevalence (village)";
                obj2[len + 15] = "Share of pop. with NCC-related ICH or hydrocephalus (village)";

                obj2[len + 16] = "Share of NCC cases with non-calcified lesions (village)";
                obj2[len + 17] = "Prop of NCC cases with extraparenchymal lesions (village)";
                obj2[len + 18] = "Share of all NCC cases that have epilepsy, active or not (village)";

                obj2[len + 19] = "Share of ICH or hydrocephalus cases that have parenchymal lesions (village)";
                //obj2[len + 21] = "Share of ICH or hydrocephalus cases that have parenchymal lesions, emigrants";
                obj2[len + 20] = "Share of NCC cases with epilepsy that have active epilepsy at the calcified stage (village)";
                obj2[len + 21] = "Share of active epilepsy & NCC cases that have at least one non-calcified lesion (village)";
                obj2[len + 22] = "Share of NCC cases with epilepsy that have active epilepsy at the calcified stage (village, with AE defined as 5 years since last seizure)";
                obj2[len + 23] = "Share of active epilepsy & NCC cases that have at least one non-calcified lesion (village, with AE defined as 5 years since last seizure)";
                obj2[len + 24] = "Share of active epilepsy cases that are under treatment (village)";

                obj2[len + 25] = "Number of deaths from ICH, village and emigrants";
                obj2[len + 26] = "Number of deaths from active epilepsy, village and emigrants";
                obj2[len + 27] = "Number of natural deaths, village and emigrants";
                obj2[len + 28] = "Number of surgeries for ICH, village and emigrants";
                //  obj2[len + 30] = "Incident ICH cases over incident epilepsy cases, village"; // keep for future potential use
                //  obj2[len + 31] = "ICH over epilepsy cases, village"; // keep for future potential use

                //  obj2[len + 30] = "Emigrants' NCC related epilepsy prev";
                //  obj2[len + 31] = "Share of emigrants with NCC-related ICH or hydrocephalus";
                //  obj2[len + 32] = "Emigrants - Share of active epilepsy cases that are under treatment";
                obj2[len + 29] = "Person.weeks with active epilepsy (cumulative, village and emigrants)"; // GB19mai
                obj2[len + 30] = "Person.weeks with treated active epilepsy (cumulative, village and emigrants)";
                obj2[len + 31] = "Person.weeks with ICH or hydrocephalus (cumulative, village and emigrants)";
                //  obj2[len + 32] = "Sum of ages of incident ICH (village)"; // GB19mai
                //  obj2[len + 33] = "Sum of ages of incident epi (village)"; // GB19mai
                //  obj2[len + 34] = "Number of ICH cases (village)"; // GB19mai
                //  obj2[len + 35] = "Number of ICH cases (emigrants)"; // GB19mai
                //  obj2[len + 36] = "Number of cases wth ICH and no extraparenchymal lesion (village)"; // GB19mai
                obj2[len + 32] = "Number of humans travelling";
                obj2[len + 33] = "Number of stranger travelers";
                obj2[len + 34] = "nb of emigrants";
                // obj2[len + 40] = "incident active epi cases (cumulative, village and emigrants)"; // GB19mai
                //   obj2[len + 41] = "incident ICH cases (cumulative, village and emigrants)"; // GB19mai
                //  obj2[len + 42] = "incident parenchymal cyst with ICH (village and emigrants)"; // GB19mai
                //  obj2[len + 43] = "incident extra-parenchymal cyst with ICH (village and emigrants)"; // GB19mai
                //  obj2[len + 44] = "incident visible cyst parenchymal (village and emigrants)"; // GB19mai
                //  obj2[len + 45] = "incident visible cyst extra-parenchymal (village and emigrants)"; // GB19mai

                obj2[len + 35] = "Humans under 12 years (village)";
                obj2[len + 36] = "Humans between 12 and 19 years (village)";
                obj2[len + 37] = "Humans between 20 and 29 years (village)";
                obj2[len + 38] = "Humans between 30 and 39 years (village)";
                obj2[len + 39] = "Humans between 40 and 49 years (village)";
                obj2[len + 40] = "Humans between 50 and 59 years (village)";
                obj2[len + 41] = "Humans above 60 years (village)";
                obj2[len + 42] = "Humans 18 years and above (village)";
                obj2[len + 43] = "Share of individuals who have ever had NCC"; // GBPIAE
                obj2[len + 44] = "Share of 60+ villagers who have ever had NCC"; // GBPIAE
                // obj2[len + 56] = "Number of cases with non calcified lesions"; // GB19mai
                // obj2[len + 57] = "Number of AE cases with non calcified lesions"; // GB19mai
                // obj2[len + 58] = "Number of epi cases with non calcified lesions"; // GB19mai
                // obj2[len + 59] = "Number of epi cases with visible lesions"; // GB19mai
                // obj2[len + 60] = "Number of AE cases with visible lesions"; // GB19mai
                //  obj2[len + 64] = "Share of individuals who have ever had an NCC lesion that disappeared"; // GBPIAE
                //  obj2[len + 61] = "Number of inactive epi cases with calcified lesions"; // GB19mai
                //  obj2[len + 66] = "Number of inactive epi cases with calcified lesions that had some lesion that disappeared"; // GBPIAE
                obj2[len + 45] = "Share of NCC cases with active epi"; // GBPIAE
                //  obj2[len + 46] = "Person.weeks with active epilepsy (cumulative, emigrants)"; // GB19mai
                //  obj2[len + 47] = "Person.weeks with treated active epilepsy (cumulative, emigrants)"; // GBPIAE
                //  obj2[len + 48] = "Person.weeks with ICH or hydrocephalus (cumulative, emigrants)"; // GBPIAE
                obj2[len + 46] = "Share of NCC cases with epilepsy and a first seizure over X years ago that have active epilepsy at the calcified stage (village)";
                obj2[len + 47] = "Share of cases with viable lesions among non-calcified lesions"; // GB26avril
                obj2[len + 48] = "Share of cases with degenerating lesions among non-calcified lesions"; // GB26avril
                //    obj2[len + 72] = "Share of AE cases among epi cases with non-calcified NCC"; // GB16mars
                //    obj2[len + 73] = "Share of individuals that have ever had a mature taenia worm"; // GB17mars
                //  obj2[len + 74] = "Share of 60+ individuals that have ever had a mature taenia worm"; // GB17mars
                obj2[len + 49] = "Share of adult NCC cases with a single lesion"; // GBRicaPlaya
                obj2[len + 50] = "Share of adult NCC cases with 11 or more lesions"; // GBRicaPlaya
                obj2[len + 51] = "Share of adult NCC cases with NCC-driven epilepsy"; // GBRicaPlaya
                obj2[len + 52] = "Share of adults with calcified NCC lesions (with or without other types of lesions)"; // GBRicaPlaya

                obj = obj2;


            }

            sim.weeklyData.add(obj);
        }
        else
        {
            sim.weeklyData.add(new Object[] {

                "Step",
                    "Week",
                    //Humans Statisitcs -----------
                    "Humans in the village",
                    "Human Taeniasis Prevalence",


                    //Pigs statistics
                    "Pigs in the village",
                    "Pigs Cysticercosis Infection Prevalence",
                    "Pigs Heavy Cysticercosis Infection Prevalence",
                    "Pigs Light Cysticercosis Infection Prevalence",
                    "Pigs Seroprevalence age > 4",
                    "Piglets Seroprevalence",
                    "Pigs Seroincidence (4-months)",
                    "Num contaminated def. sites"
            });


        }
    }

    //====================================================
    public void initHistoCysts()
    {
        pigCystsHistoLocal = new HashMap <Integer, Double>();

        //initialize the pig number of cysts histogram
        pigCystsHistoLocal.put(0, 0.0);
        for(int i = 0; i < sim.numBinsPigCystsHisto; i++)
        {
            int nB = (int)Math.round((double)(i + 1) * (double)sim.extensionBinsPigCystsHisto);
            pigCystsHistoLocal.put(nB, 0.0);
            //System.out.println("---- nB: " + nB);
        }
    }

    //====================================================
    public void initHistoCystsProg()
    {
        pigCystsHistoProgLocal = new HashMap <Integer, Double>();

        pigCystsHistoProgLocal.put(0, 0.0);
        pigCystsHistoProgLocal.put(10, 0.0);
        pigCystsHistoProgLocal.put(100, 0.0);
        pigCystsHistoProgLocal.put(1000, 0.0);
        pigCystsHistoProgLocal.put(10000, 0.0);
        pigCystsHistoProgLocal.put(100000, 0.0);
        pigCystsHistoProgLocal.put(1000000, 0.0);
    }

    //====================================================
    public void normPigHisto()
    {
        double norm = 0.0;
        for(Integer nC : pigCystsHistoLocal.keySet())
        {
            if(nC == 0)continue;
            double numOcc = pigCystsHistoLocal.get(nC);
            norm = norm + numOcc;
        }

        //System.out.println(sim.pigsBag.size() + " " + norm);

        for(Integer nC : pigCystsHistoLocal.keySet())
        {
            double numOcc = 0.0;
            if(nC == 0)
            {
                numOcc = 0.0;
            }
            else
            {
                numOcc = pigCystsHistoLocal.get(nC);
                if(numOcc > 0.0)numOcc = numOcc/norm;
                else numOcc = 0.0;
            }

            pigCystsHistoLocal.put(nC, numOcc);

        }

    }

    //====================================================
    public void normPigHistoProg()
    {
        double norm = 0.0;
        for(Integer nC : pigCystsHistoProgLocal.keySet())
        {
            if(nC == 0)continue;
            double numOcc = pigCystsHistoProgLocal.get(nC);
            norm = norm + numOcc;
        }

        for(Integer nC : pigCystsHistoProgLocal.keySet())
        {
            double numOcc;
            if(nC == 0)
            {
                numOcc = 0.0;
            }
            else
            {
                numOcc = pigCystsHistoProgLocal.get(nC);
                if(numOcc > 0.0)numOcc = numOcc/norm;
                else numOcc = 0.0;
            }

            pigCystsHistoProgLocal.put(nC, numOcc);

        }
        //System.out.println("Histo2: " + pigCystsHistoProgLocal.get(0));
        //System.out.println("norm: " + norm);

    }

    //====================================================
    public void sumPigHisto()
    {
        double norm = 0.0;
        for(Integer nC : pigCystsHistoLocal.keySet())
        {
            double numOcc = pigCystsHistoLocal.get(nC);

            numOcc = numOcc + sim.pigCystsHisto.get(nC);
            sim.pigCystsHisto.put(nC, numOcc);
        }

        //HashMap<Integer, Double> tmp = new HashMap <Integer, Double>();
    }


    //====================================================
    public void sumPigHistoProg()
    {
        double norm = 0.0;
        for(Integer nC : pigCystsHistoProgLocal.keySet())
        {
            double numOcc = pigCystsHistoProgLocal.get(nC);

            numOcc = numOcc + sim.pigCystsHistoProg.get(nC);
            sim.pigCystsHistoProg.put(nC, numOcc);
        }

        //HashMap<Integer, Double> tmp = new HashMap <Integer, Double>();
    }

    //====================================================
    public void calcHouseholdsSeroInRange()
    {
        for(int i = 0; i < sim.householdsBag.size(); i ++)
        {
            Household hh1 = (Household)sim.householdsBag.get(i);

            int numInf = 0;
            for(int h = 0; h < hh1.humans.size(); h++)
            {
                Human human = (Human)hh1.humans.get(h);
                if(human.tapewormMature)numInf++;
            }

            if(numInf == 0)continue;

            Point pHome1 = (Point)hh1.mGeometry.getGeometry();

            double pigsPositive = 0.0;
            double pigsPositiveNorm = 0.0;

            for(int j = 0; j < sim.householdsBag.size(); j ++)
            {
                Household hh2 = (Household)sim.householdsBag.get(j);

                Point pHome2 = (Point)hh2.mGeometry.getGeometry();

                if(pHome1.distance(pHome2) <= sim.simW.rangeLimit)
                {
                    for(int p = 0; p < hh2.pigs.size(); p++)
                    {
                        Pig pp = (Pig)hh2.pigs.get(p);

                        if(pp.imported)continue;

                        pigsPositiveNorm++;
                        if(pp.numCysts > 0)pigsPositive++;
                    }
                }
            }

            if(pigsPositiveNorm > 0)hh1.simPigsSeroprevalenceInRange = (double)pigsPositive/(double)pigsPositiveNorm;
            else hh1.simPigsSeroprevalenceInRange = 0;

            //System.out.println (hh1.simPigsSeroprevalenceInRange);

            if(hh1.inTheBorder)
            {
                sim.seroPrevHousesInTheBorder = sim.seroPrevHousesInTheBorder + hh1.simPigsSeroprevalenceInRange;
                sim.seroPrevHousesInTheBorderNorm++;
            }
            else 
            {
                sim.seroPrevHousesNotInTheBorder = sim.seroPrevHousesNotInTheBorder + hh1.simPigsSeroprevalenceInRange;
                sim.seroPrevHousesNotInTheBorderNorm++;
            }
        }


    }

    //====================================================
    public void resetStats()
    {
        sim.numWeeks = 0;
        sim.numWeeksPrint = 0;

        sim.infectedHumansPrevalence = 0.0;
        sim.lightInfectedPigsPrevalence = 0.0;
        sim.heavyInfectedPigsPrevalence = 0.0;
        sim.infectedPigsPrevalence = 0.0;
        sim.infectedPigsPrevalenceCysts = 0.0;

        sim.prevHumansStrangerTravelers = 0.0;

        sim.avgImmunityC = 0.0;
        sim.avgImmunityO = 0.0;
        sim.avgImmunityI = 0.0;

        sim.avgDegeneratedCystsC = 0.0;
        sim.avgDegeneratedCystsI = 0.0;

        sim.avgNumCystsFromProglottids = 0.0;
        sim.avgNumCystsFromEggs = 0.0;

        sim.avgNumCysts = 0.0;

        sim.numMeatPortionsSold = 0;
        sim.totNumMeatPortions = 0;

        sim.avgNumCystsNoNecro = 0.0;
        sim.avgDegeneratedCystsCNoNecro = 0.0;
        sim.avgDegeneratedCystsINoNecro = 0.0;
        sim.avgNumCystsFromProglottidsNoNecro = 0.0;
        sim.avgNumCystsFromEggsNoNecro = 0.0;



        for(int i = 0; i < 523; i++)
        {
            simPrevalenceByAgePigs.set(i, 0.0);
            simPrevalenceByAgePigsNorm.set(i, 0.0);
            simPrevalenceByAgePigsHisto.set(i, 0.0);
        }


        for(int i = 0; i < maxHumanAge; i++)
        {
            simPrevalenceByAgeHumans.set(i, 0.0);
            simPrevalenceByAgeHumansNorm.set(i, 0.0);
            simPrevalenceByAgeHumansHisto.set(i, 0.0);
        }


        if(sim.cystiHumans & !sim.burnin)
        {   
            sim.weeksAdultsWithNCC = 0;
            sim.sumShareAdult1CystInNCC = 0.0;
            sim.sumShareAdult11moreCystInNCC = 0.0;
            sim.averageShareAdult1CystInNCC = 0.0;
            sim.averageShareAdult11moreCystInNCC = 0.0;
            sim.sumEpiPrevalenceinAdultNCCCases = 0.0;

            sim.sumNCCPrevalence = 0.0;
            sim.sumNCCPrevalence12more = 0.0;
            sim.sumNCCPrevalence20more = 0.0;
            sim.averageNCCPrevalence20more = 0.0;
            sim.sumNCCPrevalence18more = 0.0;
            sim.sumAdultNCCPevalenceCT = 0.0;

            sim.sumShareofEpiNCCnoncalcWithActiveEpiMoyano = 0.0;
            sim.weeksShareofEpiNCCnoncalcWithActiveEpiMoyano++; // GB23mars

            sim.sumShare1CystInNCC = 0.0;
            sim.sumShare2CystsInNCC = 0.0;
            sim.sumShare3CystsInNCC = 0.0;
            sim.sumShare4CystsInNCC = 0.0;
            sim.sumShare5CystsInNCC = 0.0;
            sim.sumShareNCCcasesNonCalcified = 0.0;
            sim.sumShareofExParinNCCcases = 0.0;
            sim.sumShareofNCCcaseswithEpi = 0.0;

            sim.sumShareWithViableinNonCalcified = 0.0;
            sim.sumShareWithDegeneratedinNonCalcified = 0.0;

            sim.weeksShareWithViableinNonCalcified++;// GB26 avril

            sim.sumShareofParenchymalinICH = 0.0;
            sim.weeksShareofParenchymalinICH = 0;

            sim.sumShareofEpiNCCcalcifiedWithActiveEpi = 0.0;

            sim.weeksShareofEpiNCCcalcifiedWithActiveEpi = 0;

            sim.sumShareofEpiNCCcalcifiedWithActiveEpiCutOff = 0.0;

            sim.weeksShareofEpiNCCcalcifiedWithActiveEpiCutOff = 0;


            sim.sumShareofAEcasesThatAreNonCalcified = 0.0;

            sim.weeksShareofAEcasesThatAreNonCalcified = 0;

            sim.sumShareofEpiNCCcalcifiedWithActiveEpiMoyano = 0.0;

            sim.weeksShareofEpiNCCcalcifiedWithActiveEpiMoyano = 0;

            sim.sumShareofAEcasesThatAreNonCalcifiedMoyano = 0.0;

            sim.weeksShareofAEcasesThatAreNonCalcifiedMoyano = 0;

            sim.sumIchOverAE = sim.sumIchOverAE + 0.0;
            sim.weeksIchOverAE = 0;
        }

    }



    //end of file
}
