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

import sim.util.distribution.*;

import sim.util.geo.MasonGeometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.util.GeometricShapeFactory;

//----------------------------------------------------
public class Pig implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public Stoppable stopper;

    //public CoverPixel cpPosition = null;

    int age = 0;//in weeks

    public double weight = 0.0;

    public String gender = "";//can be female or male

    public int identity = 0;

    public int censusIdentity = 0;

    public Household household = null;

    //Parameters from CystiAgents NetLogo model
    public Boolean lightExp       = false;
    public Boolean heavyExp       = false;
    public Boolean heavyInfected  = false;
    public Boolean lightInfected  = false;
    public Boolean seropositive   = false;
    public Boolean markedForSlaughter = false;
    public Boolean eligibleP  = false;
    public double treatmentProtectedTime  = 0.0;
    public double stopSlaughterTimer  = 0.0;
    public Boolean treated = false;//if the pig was treated and the treatment protection is still acting
    public double vaccDose  = 0.0;
    public Boolean vaccinated  = false;
    public String corraled  = "never";
    public int slaughterAge  = 0;
    public double homeRange  = 0.0;
    public double homeRangeArea  = 0.0;
    public Boolean infectDIA    = false;//to infect the pig when the Deterministic 
    //individual allocation method is used
    public Boolean isCorraled  = true;

    //auxiliary variables
    public double        eggs  = 0.0;
    public double        numProglottid  = 0.0;
    public double        proglottid  = 0.0;
    public int           numContaminatedSites  = 0;
    public int           pigsInHomeRange  = 0;

    public Boolean dead = false;

    public Boolean imported  = false;

    public Boolean infect  = true;

    int numSteps = 0;

    double numDefecationSitesInHomeRange = 0.0;
    public List<Human> defecatorsInRangeList = new ArrayList<Human>();

    //for cyst new version of cystiAgents
    public int numCysts = 0;
    public int numDegeneratedCystsIc = 0;
    public int numDegeneratedCystsIcThisTimeStep = 0;
    public int numDegeneratedCystsIi = 0;
    public int numCystsFromProglottids = 0;
    public int numCystsFromEggs = 0;

    //boolean tagged = false;

    //Immunity parameters
    public double immunityO = 0.0;//immunity against oncospheres
    public double immunityC = 0.0;//immunity agaiinst cysts
    public List<Double> immunityI = new ArrayList<Double>();
    public List<Double> oldImmunityI = new ArrayList<Double>();
    public double oldImmunityO = 0.0;//immunity against oncospheres
    public double oldImmunityC = 0.0;//immunity agaiinst cysts
    public Boolean zeroImmunity = false;
    public List<Double> immunityCCreationTimeDelay = new ArrayList<Double>();
    public List<Double> immunityCDegenerationTimeDelay = new ArrayList<Double>();
    public List<Double> immunityOTimeDelay = new ArrayList<Double>();
    public List<List<Double>> immunityITimeDelay = new ArrayList<List<Double>>();

    //gestation parameters
    public Boolean pregnant = false;
    public int gestationTimer = 0;
    public int lastParityTimer = 0;
    public int parity = 0;
    public Boolean breedingSow = false;

    public int numNewCysts = 0;

    public List<Integer> immatureCystsTimeDelay = new ArrayList<Integer>();

    public Boolean necroTrue  = false;//true if the num of cyst of the pigs are 
    //are teken into account for stats

    public Boolean exposed  = false;//true if the pig was exposed to proglottids or eggs
    public int exposedDelat  = 10;//delay to develop antibodies after exposure

    public int GATESID = 0;
    public int R01ID = 0;

    public int numBands = 0;

    public int maternalAntibodiesPersistenceTimer = 0;//time after which maternal seropositivity (if adquired) vanish
    public Boolean seropositiveForMaternalProtection   = false;
    public Boolean seropositiveForExposure = false;
    public int seroConversionTimer = -100;

    //to include the pig in the seroIncidence cohort calculation
    //for interventions R01 and GATES
    public Boolean isInTheSeroIncCohortSim = false;
    public Boolean isInTheSeroIncCohortInt = false;

    public Boolean exposedIn = false;
    public Boolean exposedOut = false;

    public Geometry homeAreaShape = null; //in case landCoverAgriculturalLand is used
                                          //
    public Boolean joinedFromOtherVillage = false;

    //====================================================
    public Pig(SimState pstate, Household ph, Boolean pinfDIA, Boolean pinfect)
    {
        state = pstate;
        sim = (CystiAgents)state;

        if(!imported)sim.nTotPigs++;

        infectDIA = pinfDIA;
        infect = pinfect;

        //System.out.println("---- New Pig");

        double interval = 1.0;
        this.stopper = sim.schedule.scheduleRepeating(this, 3, interval);

        sim.pigsBag.add(this);

        household = ph;

        household.addPig(this);

        //cpPosition = household.cpPosition;

        //uncomment this to visualize the graphical interface for pigs
        //sim.pigGrid.setObjectLocation(this, 
        //       sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[0],
        //      er);

        if(sim.villageDataset.equals("TTEMP") 
        || sim.villageDataset.equals("R01") )
        {
            //if the target pig cysticercosis prevalence is not know
            //a prop of 25% of pigs is infected
            double rand = state.random.nextDouble();
            if(infect && rand < 0.25)baselineAssignCystsToPig(this);
        }
        if(sim.villageDataset.equals("GATES1"))
        {
            //if the target pig cysticercosis prevalence is not know
            //a prop of 5.5% of pigs is infected
            double rand = state.random.nextDouble();
            if(infect && rand < 0.055)baselineAssignCystsToPig(this);
        }

        if(!infectDIA)
        {
            identity = sim.pigsIds;
            sim.pigsIds++;

            getSlaughterAge("pig");

            homeRange = Math.abs(sim.homeRangeMean + state.random.nextGaussian() * sim.homeRangeSd);
            homeRange = Math.exp(homeRange);

            homeRangeArea = Math.PI * homeRange * homeRange;
            //System.out.println("Pig homeRange " + homeRange);
            //if(homeRange == 0)
            //{
            //    System.out.println("Home range = 0");
            //    System.exit(0);
            //}

            age = 0;
            //System.out.println("Pig age " + age);
        }

        if(sim.landCoverAgriculturalLand || sim.landCoverIrrigationChannels)getHomeAreaShape();

        if(household.corralUse.equals("always"))corraled = "always";
        else if(household.corralUse.equals("sometimes"))
        {
            corraled = "sometimes";
            //System.out.println("Pig corraled sometimes");
            //System.exit(0);
        }

        if(sim.immatureCystsPeriod > 0)
        {
            for(int i = 0; i < sim.immatureCystsPeriod; i++)
            {
                immatureCystsTimeDelay.add(0);
                immunityI.add(0.0);
            }
        }

        if(sim.pigsImmunity)
        {
            //latency of adquiring immunity of type C after the appearence of a cysts
            for(int i = 0; i < sim.latencyImmunityCCreation; i++)
            {
                immunityCCreationTimeDelay.add(0.0);
            }

            for(int i = 0; i < sim.latencyImmunityCDegeneration; i++)
            {
                immunityCDegenerationTimeDelay.add(0.0);
            }

            //latency of adquiring immunity of type O after the exposure
            for(int i = 0; i < sim.latencyImmunityO; i++)
            {
                immunityOTimeDelay.add(0.0);
            }

            //latency of adquiring immunity of type O after the exposure
            if(sim.immatureCystsPeriod > 0)
            {
                for(int i = 0; i < sim.immatureCystsPeriod; i++)
                {
                    List<Double> tmp = new ArrayList<Double>(); 
                    for(int j = 0; j < sim.latencyImmunityI; j++)
                    {
                        tmp.add(0.0);
                    }
                    immunityITimeDelay.add(tmp);
                }
            }

        }

        //if(numCysts != 0)System.out.println (sim.villageName + " new pig num cysts: " + numCysts);
        //if(numCysts < 0)
        //{
        //    System.out.println ("num cysts: " + numCysts);
        //    System.out.println ("negative");
        //    System.exit(0);
        //}

        //System.exit(0);

    }

    //====================================================
    public void step(SimState state)
    {
        if(dead)return;

        //if(tonguePalpation())printResume();

        //---- seroconversion of infected pigs --------
        if(numCysts > 0)
        {
            seropositive = true;
            seropositiveForExposure = true;
            //if(sim.extendedOutput)System.out.println("Pigs sepositive because infected");
        }

        //maternal antibodies seropositivity ------
        maternalAntibodiesPersistenceTimer--;
        if(maternalAntibodiesPersistenceTimer > 0)
        {
            seropositive = true;
            seropositiveForMaternalProtection = true;
            //if(sim.extendedOutput)System.out.println("Pigs sepositive for maternal protection");
        }
        else
        {
            seropositiveForMaternalProtection = false;
            if(!seropositiveForExposure)seropositive = false;
        }

        //maximum pig lifespan
        if(age > 520)
        {
            markedForSlaughter = true;
        }

        //if(slaughterAge > 1000)
        //{
        //    System.out.println ("Pig with slaughterAge > 1000 from step");
        //    System.exit(0);
        //}

        //seroconversion timer -------
        seroConversionTimer--;
        if(seroConversionTimer == 0)
        {
            seropositive = true;
            seropositiveForExposure = true;
            //if(sim.extendedOutput)System.out.println("Pigs sepositive for exposure");
        }
        

        //System.out.println ("---------------------------");
        //System.out.println ("num pigs household: " + household.pigs.size());

        //if(immunityO > 1)
        //{
        //    System.out.println ("num cysts1< 0");
        //    System.exit(0);
        //}

        if(!sim.pigsGestation)//there is no breeding sow and the gestation process is not included
        {
            if(age >= slaughterAge && stopSlaughterTimer > 0.0)markedForSlaughter = true;
            sim.pigsGen.birthPig(this);
        }
        else if(sim.pigsGestation)
        {
            gestation();
        }

        //if(age >= slaughterAge)markedForSlaughter = true;

        //if(numCysts < 0)
        //{
        //    System.out.println ("num cysts2< 0");
        //}

        //the pig is slaughtered and transformed in carne (meat)
        distributePork();

        //if(numCysts < 0)
        //{
        //    System.out.println ("num cysts3< 0");
        //}

        infectPigs();

        //if(numCysts < 0)
        //{
        //    System.out.println ("num cysts4< 0");
        //}

        age++;
        //if(age > 1000)System.out.println ("pig age: " + age);
        //System.out.println ("pig numCysts: " + numCysts);
        //if(numCysts > 0)System.out.println ("pig numCysts: " + numCysts);

        if(treated)advanceProtectionTreatment();

        //if(numCysts < 0)
        //{
        //    System.out.println ("num cysts5< 0");
        //}

        if(sim.pigsImmunity)
        {
            immunityCCystsDegeneration();//this uses results from
            immunityICystsDegeneration();//this uses results from
            updateImmunity();//this uses results from
        }

        //if(numCysts < 0)
        //{
        //    System.out.println ("num cysts6< 0");
        //    System.exit(0);
        //}
        //cystiInfectPigs do not invert order

        //System.out.println ("num pigs household: " + household.pigs.size());
    }

    //===============================================
    public void treatOXF()
    {
        treated = true;

        numCysts = (int)Math.round((1.0 - sim.oxfTreatEff) * numCysts);

        double rand = state.random.nextDouble();
        if(rand <= sim.oxfTreatEff)
        {
            if(sim.immatureCystsPeriod > 0)
            {
                for(int i = 0; i < sim.immatureCystsPeriod; i++)
                {
                    immatureCystsTimeDelay.set(i, 0);
                }
            }
        }

        //numCysts = 0;

        treatmentProtectedTime = sim.oxfProtection + 1;
        stopSlaughterTimer = sim.slaughterStopTimeAfterTreatment + 1;

        if(sim.pigsImmunity)
        {
            //latency of adquiring immunity of type C after the appearence of a cysts
            for(int i = 0; i < sim.latencyImmunityCCreation; i++)
            {
                immunityCCreationTimeDelay.add(0.0);
            }

            for(int i = 0; i < sim.latencyImmunityCDegeneration; i++)
            {
                immunityCDegenerationTimeDelay.add(0.0);
            }

            //latency of adquiring immunity of type O after the exposure
            for(int i = 0; i < sim.latencyImmunityO; i++)
            {
                immunityOTimeDelay.add(0.0);
            }

            //latency of adquiring immunity of type O after the exposure
            if(sim.immatureCystsPeriod > 0)
            {
                for(int i = 0; i < sim.immatureCystsPeriod; i++)
                {
                    List<Double> tmp = new ArrayList<Double>(); 
                    for(int j = 0; j < sim.latencyImmunityI; j++)
                    {
                        tmp.add(0.0);
                    }
                    immunityITimeDelay.add(tmp);
                }
            }
        }



    }

    //===============================================
    public  void distributePork()
    {
        if(!markedForSlaughter)return;
        //if(!imported)System.out.println(household.shpId +" Pig age at slaughter (not imported) is distributed: " + age);

        //here 0.717 is the conversion facto from total live pig weight and 
        //the carcassa weight.
        double rand = state.random.nextDouble();
        //pigs that are sold -----------------------------------
        if(rand < (sim.pigsSold))
        {
            rand = state.random.nextDouble();
            if(rand < sim.pigsExported)//the pig is exported to the si outside
            {
                double weight = 0.36 * age;
                int numPortions = (int)Math.round(weight * 0.717 / sim.perCapitaPorkConsumption);

                sim.numMeatPortionsSold = sim.numMeatPortionsSold + numPortions;

                //System.out.println("---- Pig die from distributePigs");
                //here the 10% of a highly infected pig is given back to the
                //seller to prove that it was heavily infected. This 10% is
                //consumed in the village
                if(numCysts >= sim.cystiHeavyInfected)
                {
                    //here a factor 0.1 is a piece of heavy infected pigs
                    //that are given back by the buyer
                    createMeatPortions(household, 0.1);
                }
                else die();
                return;
            }
            else
            {
                //Pigs that are distributed to other households in the village
                //System.out.println("---- Pig distributed to another household");
                int iran = state.random.nextInt(sim.householdsBag.size());
                Household hh = (Household)sim.householdsBag.get(iran);
                //System.out.println("---- Pig distributed to another household");
                rand = state.random.nextDouble();
                if(rand < sim.pigsExported)//the pig is sold but it is exported buy the buyers
                {
                    double weight = 0.36 * age;
                    int numPortions = (int)Math.round(weight * 0.717 / sim.perCapitaPorkConsumption);
                    sim.numMeatPortionsSold = sim.numMeatPortionsSold + numPortions;

                    //System.out.println("---- Pig die from distributePigs");
                    if(numCysts >= sim.cystiHeavyInfected)
                    {
                        //here a factor 0.1 is a piece of heavy infected pigs
                        //that are given back by the buyer
                        createMeatPortions(hh, 0.1);
                    }
                    else die();
                    return;
                }
                else // the pig is slaughterd at home by the buyers
                {
                    createMeatPortions(hh, 1.0);
                    return;
                }
            }
        }

        //System.out.println("---- Pig slaughtered at home by the owners");
        createMeatPortions(household, 1.0);//the pig is slaughtered at home by the owners
        return;
    }

    //===============================================
    public  void createMeatPortions(Household hh, double proportion)
    {
        necroTrue = true;
        //the pig weight is determined by its age
        //double weight = 14.6 + 0.2884 * age;
        double weight = 0.36 * age;

        //the factor of pig - meat convertion is 0.7 
        //double meat = 50 * 0.7 * proportion;
        //0.385 is the maximum weekly pork consumption per person in Peru
        //int weeklyMeatPortions = (int)Math.round(meat / 0.385);

        //0.155 is the proportion of entrails (Thesis Dafne Ramos)
        //no cysts here
        int portionsEntrails = (int)Math.round(proportion * weight * 0.155 / sim.perCapitaPorkConsumption);

        //0.562 is the proportion of meat and subcutaneous fat skin and bones(Thesis Dafne Ramos)
        //cysts here
        int portionsMeatFat = (int)Math.round(proportion * weight * 0.562 / sim.perCapitaPorkConsumption);

        //System.out.println("---- Num weekly meat protions: " + weeklyMeatPortions);

        double cystsPerPortMeatFat = (double)numCysts / (double)portionsMeatFat;

        //System.out.println("---- Num cysts per portion: " + cystsPerPortMeatFat);

        //System.out.println("---- Num cysts per portion: " + cystsPerPort);

        int statsCysts = 0;
        //System.out.println("---------------------------------");
        for(int i = 0; i < portionsMeatFat; i++)
        {
            //int intCystsPerPortMeatFat = sim.poissonProglottids.nextInt(cystsPerPortMeatFat * sim.pHumanCyst);
            //System.out.println("---- Num cysts this portion: " + intCystsPerPortMeatFat);
            int irand = sim.poissonProglottids.nextInt(cystsPerPortMeatFat);

            double nC = 0.0;
            if(statsCysts >= numCysts)nC = 0.0;
            else 
            {
                statsCysts = statsCysts + irand;
                nC = (double)irand;
            }

            //System.out.println("---- Num cysts this portion: " + nC);

            MeatPortion meatP = new MeatPortion(sim, this, nC);
            //MeatPortion meatP = null;
            //if(numCysts > 0)meatP = new MeatPortion(sim, this, 1.0);
            //else meatP = new MeatPortion(sim, this, 0.0);


            meatP.household = hh;
            hh.addMeatPortionInfected(meatP);
        }

        //System.out.println("totNumCysts: " + numCysts + " num assigned cysts: " + statsCysts);

        for(int i = 0; i < portionsEntrails; i++)
        {
            MeatPortion meatP = new MeatPortion(sim, this, 0);
            meatP.household = hh;
            hh.addMeatPortionNoInfected(meatP);
            //System.out.println("Not infected meat portion");
        }



        //System.out.println(sim.villageName + " Total Number of meat Portions in the village: " + sim.meatPortionsBag.size());

        die();
        return;
    }

    //===============================================
    public  void die()
    {
        if(dead)return;

        if(exposedIn)sim.nPigsExposedIn++;
        if(exposedOut)sim.nPigsExposedOut++;

        if(necroTrue)
        {
            sim.avgDegeneratedCystsCTimestep = sim.avgDegeneratedCystsCTimestep + numDegeneratedCystsIc;
            sim.avgDegeneratedCystsITimestep = sim.avgDegeneratedCystsITimestep + numDegeneratedCystsIi;

            sim.avgNumCystsFromProglottidsTimestep = sim.avgNumCystsFromProglottidsTimestep + numCystsFromProglottids;
            sim.avgNumCystsFromEggsTimestep = sim.avgNumCystsFromEggsTimestep + numCystsFromEggs;

            sim.avgNumCystsTimestep = sim.avgNumCystsTimestep + numCysts;

            sim.avgCystsStats++;
        }

        //System.out.println("---- Pig dies ... RIP");

        //printResume();
        //household.printResume();

        //System.out.println("--------------------------------");
        //System.out.println(household.shpId + " Pig in tha house: " + (household.pigs.size()));
        //System.out.println(household.shpId + " target number of pigs: " + household.targetNumOfPigs);

        sim.pigsBag.remove(this);
        household.removePig(this);

        this.stopper.stop();

        dead = true;

        return;
    }

    //===============================================
    public  void baselineAssignCystsToPig(Pig pig)
    {
        //proglottid = 2.0;
        //int iran = state.random.nextInt();
        int iran = sim.poissonProglottids.nextInt(100);
        proglottid = iran;


        int irand = sim.poissonProglottids.nextInt();
        double fact = (double)irand * proglottid * 7 * sim.pigPHomeArea;
        pig.numCysts = pig.numCysts + (int)Math.round(fact);


        //iran = state.random.nextInt(2000);
        iran = sim.poissonProglottids.nextInt(100);
        //eggs = 2.0;
        eggs = iran;
        irand = sim.poissonEggs.nextInt();
        fact = (double)irand * eggs * sim.pigPHomeArea; 
        int ifact = (int)Math.round(fact);
        pig.numCysts = pig.numCysts + ifact;
        //System.out.println(pig.numCysts);
        //System.exit(0);

    }

    //===============================================
    public int getNumHumanTapeWorm()//num of humans in the entire village
        //that are tapeworm carriers
    {
        int stats = 0;
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);
            if(h.tapeworm)stats++;
        }
        return stats;
    }

    //===============================================
    public void getContaminationInRangeFromCounter()
    {
        numContaminatedSites = 0;
        eggs = 0.0;
        proglottid = 0.0;
        numProglottid = 0.0;

        //System.out.println("Pig: " + identity + " house: " + household.simId  + " Num defecators: " + defecatorsInRangeList.size());

        for(int i = 0; i < defecatorsInRangeList.size(); i++)
        {
            Human h = (Human)defecatorsInRangeList.get(i);

            if(h.sewerUser)continue;
            //System.out.println("Egg from counter");
            //System.exit(0);

            DefecationSite defecationSite = h.defecationSite;

            List <Pig> pigList = sim.defecationSitesPigs.get(defecationSite);

            if(pigList != null && pigList.contains(this))
            {
                if(defecationSite.eggs || defecationSite.proglottid)numContaminatedSites++;
                if(!h.latrineUser)
                {
                    if(defecationSite.eggs)eggs = eggs + sim.propDefInsideRadius;
                    if(defecationSite.proglottid)
                    {
                        numProglottid = numProglottid + sim.propDefInsideRadius;

                        //if(h.strangerTraveler)
                        //{
                        //    System.out.println("proglot inf from strangertraveler");
                        //    System.out.println(numProglottid);
                        //}

                        //the number of proglottids is diveded by the number of
                        //pigs having access to it
                        proglottid = proglottid + (sim.propDefInsideRadius / (double)pigList.size());
                        //proglottid = proglottid + 1.0;
                    }
                }
                else
                {
                    if(defecationSite.eggs)eggs = eggs + (1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                    if(defecationSite.proglottid)
                    {
                        //if(h.strangerTraveler)System.out.println("proglot inf from strangertraveler");
                        numProglottid = numProglottid + (1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                        //the number of proglottids is diveded by the number of
                        //pigs having access to it
                        proglottid = proglottid + ((1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius) / (double)pigList.size();
                        //proglottid = proglottid + 1.0;
                    }
                }
            }
        }

    }

    //===============================================
    public  void infectPigs()
    {
        Boolean printOut = false;

        numNewCysts = 0;

        if(isCorraled)return;
        if(treated)return;

        int irand = 0;
        double rand = 0.0;

        getContaminationInRangeFromCounter();

        //the proglottid is divided by the number of defecation sites in the home
        //range
        //if(numDefecationSitesInHomeRange != 0)proglottid = proglottid/numDefecationSitesInHomeRange;

        //System.out.println("-----");
        //System.out.println("numContaminatedSites: " + numContaminatedSites);
        //System.out.println("sim.numContaminatedSites: " + sim.numContaminatedSites);
        //System.out.println("proglottid: " + proglottid);
        //System.out.println("sim.proglottid: " + sim.proglottid);
        //System.out.println("numProglottid: " + numProglottid);
        //System.out.println("eggs: " + eggs);
        //System.out.println("sim.eggs: " + sim.eggs);
        //System.out.println("defec sites in home range: " + numDefecationSitesInHomeRange);
        //System.out.println("num defec sites: " + sim.numActiveDefecationSites);
        //System.out.println("num cysts: " + numCysts);

        //if(eggs != 0)System.exit(0);

        if(printOut)System.out.println("-----------------------");
        //Exposure in home range area
        if(numContaminatedSites > 0)
        {
            exposedIn = true;

            //System.out.println("-----");
            //System.out.println("numContaminatedSites: " + numContaminatedSites);
            //System.out.println("proglottid: " + proglottid);
            //System.out.println("numProglottId: " + numProglottId);
            //System.out.println("HI * proglottid: " + proglottid * sim.heavyInfPig);
            //System.out.println("eggs: " + eggs);
            //System.out.println("eggs * LI: " + eggs * sim.lightInfPig);

            if(eggs > 0)
            {
                //System.out.println("Pig infected eggs home range");
                irand = sim.poissonEggs.nextInt();
                //irand = state.random.nextInt((int)Math.round(sim.pigEggsInf));
                //System.out.println("irand ova: " + irand);
                double factSero = eggs * sim.pigPHomeArea; 

                double fact = (double)irand * factSero;

                if(sim.pigsImmunity)fact = fact * (1 - immunityO);

                int ifact = (int)Math.round(fact);

                //numCysts = numCysts + ifact;

                numNewCysts = numNewCysts + ifact;
                numCystsFromEggs = numCystsFromEggs + ifact;

                if(printOut)System.out.println("fact eggs home range: " + fact);

                //seroconversion
                if(!seropositiveForExposure && seroConversionTimer < 0)
                {
                    rand = state.random.nextDouble();
                    if(isAPiglet())
                    {
                        if(rand < (sim.seroConvertEggsPiglets * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }
                    }
                    else
                    {
                        if(rand < (sim.seroConvertEggs * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }

                    }
                }

            }

            if(proglottid > 0)
            {
                irand = sim.poissonProglottids.nextInt();
                //irand = state.random.nextInt((int)Math.round(sim.pigProglotInf));

                //System.out.println("irand proglott: " + irand);

                double factSero = proglottid * 7 * sim.pigPHomeArea;
                double fact = (double)irand * factSero;

                if(sim.pigsImmunity)fact = fact * (1 - immunityO);

                int ifact = (int)Math.round(fact);

                if(printOut)System.out.println("fact proglott home range: " + fact);

                //numCysts = numCysts + (int)Math.round(fact);

                numNewCysts = numNewCysts + ifact;
                numCystsFromProglottids = numCystsFromProglottids + ifact;

                //seroconversion
                if(!seropositiveForExposure && seroConversionTimer < 0)
                {
                    rand = state.random.nextDouble();
                    if(isAPiglet())
                    {
                        if(rand < (sim.seroConvertProglottidsPiglets * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }
                    }
                    else
                    {
                        if(rand < (sim.seroConvertProglottids * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }
                    }
                }


            }
        }
        //System.out.println("numCysts: " + numCysts);

        //Exposure out of home range area
        if((sim.numContaminatedSites - numContaminatedSites) > 0)
        {
            if((sim.eggs - eggs) > 0)
            {
                exposedOut = true;

                //System.out.println("Pig infected eggs out of home range");
                irand = sim.poissonEggs.nextInt();
                //System.out.println("irand ova: " + irand);

                double factSero = (sim.eggs - eggs);  
                factSero = factSero * (1 - sim.pigPHomeArea);

                //normalized to the number of household contamination areas
                factSero = factSero / (double)((sim.householdsBag.size() - 1) * sim.villageHouseDensityFactor);

                double fact = (double)irand * factSero;

                if(sim.pigsImmunity)fact = fact * (1 - immunityO);

                int ifact = (int)Math.round(fact);

                //numCysts = numCysts + (int)Math.round(fact);

                numNewCysts = numNewCysts + ifact;
                numCystsFromEggs = numCystsFromEggs + ifact;

                if(printOut)System.out.println("fact eggs out: " + fact);

                //seroconversion
                if(!seropositiveForExposure && seroConversionTimer < 0)
                {
                    rand = state.random.nextDouble();
                    if(isAPiglet())
                    {
                        if(rand < (sim.seroConvertEggsPiglets * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }
                    }
                    else
                    {
                        if(rand < (sim.seroConvertEggs * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }

                    }
                }
            }

            if((sim.proglottid - numProglottid) > 0)
            {
                exposedOut = true;

                irand = sim.poissonProglottids.nextInt();

                double factSero = 7;

                factSero = factSero * (sim.proglottid - numProglottid);
                factSero = factSero / (sim.pigsBag.size()); 
                //factSero = factSero / (sim.numActiveDefecationSites - numDefecationSitesInHomeRange);
                factSero = factSero * (1.0 - sim.pigPHomeArea);

                //if((int)Math.round(fact) < 0)
                //{
                //    System.out.println("fact < 0!!!");
                //    System.out.println((sim.numActiveDefecationSites - numDefecationSitesInHomeRange));

                //}

                double fact = (double)irand * factSero;

                if(sim.pigsImmunity)fact = fact * (1 - immunityO);

                int ifact = (int)Math.round(fact);

                //numCysts = numCysts + (int)Math.round(fact);

                numNewCysts = numNewCysts + ifact;
                numCystsFromProglottids = numCystsFromProglottids + ifact;

                //seroconversion
                if(!seropositiveForExposure && seroConversionTimer < 0)
                {
                    rand = state.random.nextDouble();
                    if(isAPiglet())
                    {
                        if(rand < (sim.seroConvertProglottidsPiglets * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }
                    }
                    else
                    {
                        if(rand < (sim.seroConvertProglottids * factSero) )
                        {
                            seroConversionTimer = sim.seroConversionLatency;
                        }
                    }
                }

                if(printOut)System.out.println("fact proglottid out: " + fact);

            }
        }
        //System.out.println("num cysts: " + numCysts);

        if(sim.immatureCystsPeriod > 0)
        {
            //create the new cysts with a delay of immatureCystsPeriod (around 10 weeks)
            numCysts = numCysts + immatureCystsTimeDelay.get(sim.immatureCystsPeriod - 1);

            List<Integer> tmp = new ArrayList<Integer>();
            for(int i = 0; i < sim.immatureCystsPeriod; i++)
            {
                if(i == 0)tmp.add(numNewCysts);
                else
                {
                    tmp.add(immatureCystsTimeDelay.get(i - 1));
                }
            }
            immatureCystsTimeDelay = tmp;
        }
        else numCysts = numCysts + numNewCysts;

    }

    //===============================================
    public  void advanceProtectionTreatment()
    {
        if(!vaccinated)
        {
            treatmentProtectedTime--;        
            if(treatmentProtectedTime == 0)
            {
                treated = false; 
            }

            stopSlaughterTimer--;        
            if(stopSlaughterTimer == 0)
            {
                treated = false; 
            }
        }
    }


    //====================================================
    public void printResume()
    {
        if(!sim.extendedOutput)return;
        System.out.println ("village: " + sim.villageName);
        //System.out.println(" ");
        System.out.println("---- Pig Resume ------------------");
        System.out.println("pig Id: " + identity);
        System.out.println("pig household Id: " + household.simId);
        System.out.println("pig gender: " + gender);
        System.out.println("pig age: " + age);
        System.out.println("pig numCysts: " + numCysts);
        System.out.println("pig slaughterAge: " + slaughterAge);

        if(imported == true)System.out.println("pig imported");
        else System.out.println("pig not imported");

        if(seropositive == true)System.out.println("pig seropositive");
        else System.out.println("pig not seropositive");

        if(markedForSlaughter == true)System.out.println("pig markedForSlaughter");
        else System.out.println("pig not markedForSlaughter");

        if(seropositiveForMaternalProtection == true)System.out.println("pig seropositiveForMaternalProtection");
        else System.out.println("pig not seropositiveForMaternalProtection");

        if(pregnant == true)System.out.println("pig gestating");

        if(breedingSow == true)System.out.println("pig breedingSow <<<<<<<<<<<");

    }

    //====================================================
    public int tapewormInHomeRange()
    {
        int stats = 0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human human = (Human)sim.humansBag.get(i);
            if(!human.tapeworm)continue;

            //CoverPixel cpHuman = human.cpPosition;

            //double dist = (cpHuman.xcor - cpPosition.xcor) * (cpHuman.xcor - cpPosition.xcor);
            //dist = dist + (cpHuman.ycor - cpPosition.ycor) * (cpHuman.ycor - cpPosition.ycor);
            //dist = Math.sqrt(dist);
            //dist = dist * sim.geoCellSize;

            Point hPoint = human.household.geoPoint;
            Point pPoint = household.geoPoint;

            //double dist = (cp.xcor - pig.cpPosition.xcor) * (cp.xcor - pig.cpPosition.xcor);
            //dist = dist + (cp.ycor - pig.cpPosition.ycor) * (cp.ycor - pig.cpPosition.ycor);

            //double dist = (hPoint.getX() - pPoint.getX()) * (hPoint.getX() - pPoint.getX());
            //dist = dist + (hPoint.getY() - pPoint.getY()) * (hPoint.getY() - pPoint.getY());
            //dist = Math.sqrt(dist);

            double dist = hPoint.distance(pPoint);

            //System.out.println("pig homerange: " + pig.homeRange);
            if(dist <= homeRange)
            {
                stats++;
            }
        }

        return stats;

    }


    //===============================================
    public void countDefecSites(Pig pig)
    {
        pig.numDefecationSitesInHomeRange = 0;
 
        if(!sim.useLandCover)
        {
            int stats = 0;
            for(int i = 0; i < sim.humansBag.size(); i++)
            {
                Human h = (Human)sim.humansBag.get(i);

                if(h.sewerUser)continue;

                //if(h.latrineUser)continue; 
                stats++;

                //CoverPixel cp = h.cpPositionDefecationSite;

                Point defPoint = h.defecationSite.pointPos;

                Point pPoint = pig.household.geoPoint;

                //double dist = (cp.xcor - pig.cpPosition.xcor) * (cp.xcor - pig.cpPosition.xcor);
                //dist = dist + (cp.ycor - pig.cpPosition.ycor) * (cp.ycor - pig.cpPosition.ycor);
                //dist = Math.sqrt(dist);
                //dist = dist * sim.geoCellSize;

                //double dist = (defPoint.getX() - pPoint.getX()) * (defPoint.getX() - pPoint.getX());
                //dist = dist + (defPoint.getY() - pPoint.getY()) * (defPoint.getY() - pPoint.getY());
                //dist = Math.sqrt(dist);
                //dist = dist * sim.geocellsize;

                double dist = defPoint.distance(pPoint);

                if(dist <= pig.homeRange)
                {
                    if(!h.latrineUser)pig.numDefecationSitesInHomeRange = pig.numDefecationSitesInHomeRange + sim.propDefInsideRadius;
                    else pig.numDefecationSitesInHomeRange 
                        = pig.numDefecationSitesInHomeRange + (1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                    pig.defecatorsInRangeList.add(h);
                }
            }
            //System.out.println("Pig " + identity  + " household: " + household.simId + " homeRange: " + homeRange + " num defecation sites within home range: " + numDefecationSitesInHomeRange);
            //System.out.println("Human outdoor defecation sites: " + stats);
            //System.exit(0);
        }
        else
        {

            int stats = 0;
            for(int i = 0; i < sim.humansBag.size(); i++)
            {
                Human h = (Human)sim.humansBag.get(i);

                if(h.sewerUser)continue;

                //if(h.latrineUser)continue; 
                stats++;

                if(pig.homeAreaShape.contains((Geometry)h.defecationSite.pointPos))
                {
                    if(!h.latrineUser)pig.numDefecationSitesInHomeRange = pig.numDefecationSitesInHomeRange + sim.propDefInsideRadius;
                    else pig.numDefecationSitesInHomeRange 
                        = pig.numDefecationSitesInHomeRange + (1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                    pig.defecatorsInRangeList.add(h);
                }
            }
            //System.out.println("Pig " + identity  + " household: " + household.simId + " homeRange: " + homeRange + " num defecation sites within home range: " + numDefecationSitesInHomeRange);
            //System.out.println("Human outdoor defecation sites: " + stats);

            //System.exit(0);
        }
    }


//===============================================
public void immunityCCystsDegeneration()
{
    if(numCysts > 0)
    {
            int nd = sim.poissonImmunity.nextInt(immunityC * ((double)numCysts));
            //System.out.println("nd: " + nd);

            if(nd > numCysts)nd = numCysts;

            numDegeneratedCystsIc = numDegeneratedCystsIc + nd;
            numCysts = numCysts - nd;
            numDegeneratedCystsIcThisTimeStep = nd;
            //System.exit(0);

            //immunityC = immunityC + sim.immunityCfd * nd;
        }
    }

    //===============================================
    public void immunityICystsDegeneration()
    {
        if(sim.immatureCystsPeriod > 0)
        {
            for(int i = 0; i < sim.immatureCystsPeriod; i++)
            {
                int numImmatureCysts = immatureCystsTimeDelay.get(i);
                int nd = sim.poissonImmunity.nextInt(immunityI.get(i) * ((double)numImmatureCysts));

                //if(immunityI.get(i) > 0 )System.out.println(immunityI.get(i));

                if(nd > numImmatureCysts)nd = numImmatureCysts;

                numDegeneratedCystsIi = numDegeneratedCystsIi + nd;
                numImmatureCysts = numImmatureCysts - nd;
                //System.exit(0);

                immatureCystsTimeDelay.set(i, numImmatureCysts);
                //if(immatureCystsTimeDelay.get(i) > 0 )System.out.println(immatureCystsTimeDelay.get(i));
            }
        }
    }

    //===============================================
    public void updateImmunity()
    {
        if(zeroImmunity)return;

        updateImmunityC();
        updateImmunityI();
        updateImmunityO();
    }

    //===============================================
    public void updateImmunityC()
    {
        //-----------------------------------------------------------------
        //update cysts immunityC  -------------------------------------------
        //System.out.println("latency immunityC: " + sim.latencyImmunityCCreation);

        //age-related immunity of type C 
        immunityC = immunityC + sim.immunityCfac;

        //upgrade immunityC for degeneration ----------------
        if(sim.latencyImmunityCCreation > 0)
        {
            immunityC = immunityC + immunityCCreationTimeDelay.get(sim.latencyImmunityCCreation - 1);

            List<Double> tmp = new ArrayList<Double>();
            for(int i = 0; i < sim.latencyImmunityCCreation; i++)
            {
                if(i == 0)tmp.add((double)numNewCysts * sim.immunityCfc);
                else
                {
                    tmp.add(immunityCCreationTimeDelay.get(i - 1));
                }
            }
            immunityCCreationTimeDelay = tmp;
        }
        else immunityC = immunityC + (double)numNewCysts * sim.immunityCfc;

        //upgrade immunityC for degeneration ----------------
        if(sim.latencyImmunityCDegeneration > 0)
        {
            immunityC = immunityC + immunityCDegenerationTimeDelay.get(sim.latencyImmunityCDegeneration - 1);

            List<Double> tmp = new ArrayList<Double>();
            for(int i = 0; i < sim.latencyImmunityCDegeneration; i++)
            {
                if(i == 0)tmp.add((double)numDegeneratedCystsIcThisTimeStep * sim.immunityCfd);
                else
                {
                    tmp.add(immunityCDegenerationTimeDelay.get(i - 1));
                }
            }
            immunityCDegenerationTimeDelay = tmp;
        }
        else immunityC = immunityC + (double)numDegeneratedCystsIcThisTimeStep * sim.immunityCfd;


        if(immunityC > 1.0)immunityC = 1.0;

        //System.out.println("immunityC : " + immunityC);


    }


    //===============================================
    public void updateImmunityI()
    {
        //------------------------------------------------------------------
        //update cysts immunityI  -------------------------------------------
        if(sim.immatureCystsPeriod > 0)
        {
            for(int i = 0; i < sim.immatureCystsPeriod; i++)
            {
                immunityI.set(i , (immunityI.get(i) + sim.immunityIfac));//age-related immunity of type I 
            }

            //shift in time the immunityI values
            for(int i = 0; i < sim.immatureCystsPeriod; i++)
            {
                if(sim.latencyImmunityI > 0)
                {
                    List<Double> iTDel = immunityITimeDelay.get(i); 

                    //immunityI.set(i, iTDel.get(sim.latencyImmunityI - 1));

                    List<Double> tmp = new ArrayList<Double>();
                    for(int j = 0; j < sim.latencyImmunityI; j++)
                    {
                        //System.out.println(immatureCystsTimeDelay.get(i));
                        if(j == 0)tmp.add(immatureCystsTimeDelay.get(i) * sim.immunityIs);
                        else
                        {
                            tmp.add(iTDel.get(j - 1));
                        }
                    }
                    //System.out.println(tmp);
                    immunityITimeDelay.set(i, tmp);
                }
                else
                {
                    immunityI.set(i, (immunityI.get(i) + immatureCystsTimeDelay.get(i)));
                }

                if(immunityI.get(i) > 1.0)immunityI.set(i, 1.0);

            }


            //assign the increments of immuntyI to the stages of cysts immature development
            if(sim.latencyImmunityI > 0)
            {
                for(int i = (sim.latencyImmunityI - 1); i < sim.immatureCystsPeriod; i = i + sim.latencyImmunityI)
                {
                    List<Double> iTDel = immunityITimeDelay.get(i); 

                    for(int s = (i - sim.latencyImmunityI + 1); s < (i + 1); s++)
                    {
                        immunityI.set(s, (immunityI.get(s) + iTDel.get(sim.latencyImmunityI - 1)));
                    }
                }
            }






        }



    }

    //===============================================
    public void updateImmunityO()
    {


        //-----------------------------------------------------------------
        //update oncospheres immunity ------------------------------------

        //age-related immunity of type O 
        immunityO = immunityO + sim.immunityOfac;

        //home range contribution
        double iO = 0.0;
        iO = iO + (proglottid * 7 * sim.pigPHomeArea) * sim.immunityOfp;//proglottids related immunity increase
        iO = iO + (eggs * sim.pigPHomeArea) * sim.immunityOfp * sim.immunitype;//eggs related immunity increase

        //not-home range contribution
        double fact = 7 * (sim.proglottid  - numProglottid);
        fact = fact / (sim.pigsBag.size()); 
        fact = fact / (sim.numActiveDefecationSites - numDefecationSitesInHomeRange);
        fact = fact * (1.0 - sim.pigPHomeArea);
        iO = iO + fact * sim.immunityOfp;//proglottids related immunity increase

        fact = (sim.eggs - eggs);  
        fact = fact * (1 - sim.pigPHomeArea);
        fact = fact / (double)(sim.householdsBag.size() - 1);
        iO = iO + fact * sim.immunityOfp * sim.immunitype;

        if(sim.latencyImmunityO > 0)
        {
            immunityO = immunityO + immunityOTimeDelay.get(sim.latencyImmunityO - 1);

            List<Double> tmp = new ArrayList<Double>();
            for(int i = 0; i < sim.latencyImmunityO; i++)
            {
                if(i == 0)tmp.add(iO);
                else
                {
                    tmp.add(immunityOTimeDelay.get(i - 1));
                }
            }
            immunityOTimeDelay = tmp;
        }
        else immunityO = immunityO + iO;
        if(immunityO > 1.0)immunityO = 1.0;

        //System.out.println("immunityO : " + immunityO);

    }

    //===============================================
    public void gestation()
    {
        if(gender.equals("male") || !breedingSow)return;

        //System.out.println("Pig gestation   --------------------------------");

        //upgrade timers
        gestationTimer--;
        lastParityTimer--;

        //put to zero immunity of sows for pregnancy
        if(sim.pigsImmunity && !zeroImmunity)
        {
            if(pregnant && gestationTimer <= sim.startSowZeroImmunity)
            {
                zeroImmunity = true;

                if(sim.immatureCystsPeriod > 0)
                {
                    oldImmunityI = immunityI;

                    for(int i = 0; i < sim.immatureCystsPeriod; i++)
                    {
                        immunityI.set(i, 0.0);
                    }
                }

                oldImmunityO = immunityO;
                oldImmunityC = immunityC;

                immunityO = 0.0;
                immunityC = 0.0;
            }
        }
        //restore immunity of sows after pregnancy
        if(sim.pigsImmunity && zeroImmunity)
        {
            if((sim.betweenParityPeriod - lastParityTimer) >= sim.endSowZeroImmunity)
            {
                zeroImmunity = false;

                if(sim.immatureCystsPeriod > 0)
                {
                    immunityI = oldImmunityI;

                    for(int i = 0; i < sim.immatureCystsPeriod; i++)
                    {
                        oldImmunityI.set(i, 0.0);
                    }
                }

                immunityO = oldImmunityO;
                immunityC = oldImmunityC; 

                oldImmunityO = 0.0;
                oldImmunityC = 0.0;
            }
        }


        //----- gestation ----------
        if(pregnant)
        {
            if(gestationTimer >= 0)return;

            //System.out.println("----------------------");
            pregnant = false;
            lastParityTimer = (int)Math.round(sim.betweenParityPeriod + state.random.nextGaussian() * 0.15 * sim.betweenParityPeriod);
            if(lastParityTimer < 2)lastParityTimer = 2;

            //System.out.println("sim.betweenParity Period: " + sim.betweenParityPeriod);
            //System.out.println("lastParityTimer: " + lastParityTimer);

            parity++;

            //generates a random of piglets > 0 and < 2
            int nPiglets = state.random.nextInt(sim.numPigletsPerFarrow);
            nPiglets++;
            //System.out.println(nPiglets);
            for(int i = 0; i < nPiglets; i++)
            {
                Pig pig = sim.pigsGen.birthPig(this);
            }
            //System.out.println("A piglet is born!");
            //birthPig();

        }
        else // not pregnant
        {
            if(age <= sim.sexualMaturityAge)return;
            if(lastParityTimer >= 0)return;

            pregnant = true;
            gestationTimer = sim.gestationTimeLenght; 
        }
    }


    //===============================================
    public Boolean tonguePalpation()
    {

        double rand = state.random.nextDouble();

        //if(numCysts > sim.nCystsTonguePositive && rand > sim.probTongueFalseNegative)return true;
        //if(numCysts < sim.nCystsTonguePositive && rand < sim.probTongueFalsePositive)return true;

        if(numCysts > 0 && rand < 0.025)return false;

        if((numCysts <= 1000 && numCysts > 100) && rand < 0.9 * sim.fleckerFact)return true;
        if((numCysts <= 100 && numCysts > 10) && rand < 0.55 * sim.fleckerFact)return true;
        if((numCysts <= 10 && numCysts >= 1) && rand < 0.23 * sim.fleckerFact)return true;

        return false;
    }

    //===============================================
    public Boolean isAPigletSeroInc()
    {
            if(age >= sim.cohortSeroIncAges.get(0)
                    && age <= sim.cohortSeroIncAges.get(1))return true;
            else return false;
    }

    //===============================================
    public Boolean isAPiglet()
    {
            if(age <= sim.cohortSeroIncAges.get(1))return true;
            else return false;
    }

    //===============================================
    public Boolean isAgePiglet()
    {
            if(age >= 8
                    && age <= 14)return true;
            else return false;
    }

    //===============================================
    public Boolean isAgeTransition()
    {
            if(age >= 15
                    && age <= 20)return true;
            else return false;
    }

    //===============================================
    public Boolean isAgeYoungPig()
    {
            if(age >= 21
                    && age <= 37)return true;
            else return false;
    }

    //===============================================
    public Boolean isAgeAdultPig()
    {
            if(age >= 38)return true;
            else return false;
    }


    //===============================================
    public void getSlaughterAge(String what)
    {
        double ageFact = 0.0;
        if(what.equals("breedingSow"))ageFact = 6.0;
        else if(what.equals("pig"))ageFact = 1.0;

        //System.out.println("==================");
        double sa = Math.abs(sim.slaughterAgeMean + state.random.nextGaussian() * sim.slaughterAgeSd);
        //System.out.println("sa: " + sa);
        //System.out.println("exp: " + Math.exp(sa));
        slaughterAge = (int)Math.round(ageFact  * sim.weeksInAMonth * Math.exp(sa));
        //System.out.println("Pig slaughter age " + slaughterAge);
        //if(slaughterAge < 2)slaughterAge = (int)Math.round(sim.slaughterAgeMean);
        if(slaughterAge < 2)
        {
            //System.out.println("-----");
            //System.out.println("Pig slaughter age " + slaughterAge);
            int irand = state.random.nextInt(4);
            slaughterAge = (int)Math.round(slaughterAge + irand);
            //System.out.println("Pig slaughter age " + slaughterAge);
        }
        //System.out.println("Pig age " + age);
        //System.out.println("Pig slaughter age " + slaughterAge);
        //System.exit(0);

        //if(what.equals("breedingSow"))System.out.println("Pig slaughter age " + slaughterAge);

        //pigs lifespan limit is 10 year = 522 weeks
        if(slaughterAge > 522)
        {
            int irand = state.random.nextInt(150);
            slaughterAge = 522 - irand;
            //System.out.println("new slaughterAge: " + slaughterAge);
        }

        //System.out.println("Pig slaughterAge params " + sim.slaughterAgeMean + " " + sim.slaughterAgeSd);
        //Systemout.println("Pig slaughterAge " + slaughterAge);
        //System.out.println("New Pig " + identity  + " household: " + household.simId + " homeRange: " + homeRange + " num defecation sites within home range: " + numDefecationSitesInHomeRange);




    }



    //===============================================
    public void getHomeAreaShape()
    {
        Boolean printOut = false;


        int stats = 0;

        GeometryFactory fact =  new GeometryFactory();

        double delta = 5;
        double range = homeRange - delta;
        int numMaxCircles = 200;

        Point cPoint = household.geoPoint;
        Coordinate coord = cPoint.getCoordinate();

        Boolean doLoop = true;

        if(printOut)if(sim.extendedOutput)System.out.println("");
        if(printOut)if(sim.extendedOutput)System.out.println("---- =================================================");
        if(printOut)if(sim.extendedOutput)System.out.println("---- new pig shapeArea calculations ------------------");
        if(printOut)if(sim.extendedOutput)System.out.println("Home area: " + homeRangeArea);

        Geometry resultingArea = null;
        Geometry intersection = null;

        Geometry circle = null;

        //GeometricShapeFactory gsf = new GeometricShapeFactory();

        while(doLoop)
        {

            range = range + delta;
            
            //gsf.setSize(2.0 * range);
            int numPoints = (int)Math.round((2.0 * Math.PI  * range)/5.0);//a point every 5 meters
            //gsf.setNumPoints(numPoints);


            //gsf.setBase(coord);
            //configure the circle as appropriate
            //circle = gsf.createCircle();

            circle = sim.utils.createCircle(coord, range, numPoints);

            Bag bag = null;

            resultingArea = (Geometry)circle;

            if(sim.landCoverAgriculturalLand)
            {
                bag = sim.agriculturalLand.getGeometries();

                for(int i = 0; i < bag.size(); i++)
                {
                    MasonGeometry mg = (MasonGeometry)bag.get(i);
                    Geometry agri = (Geometry)mg.getGeometry(); 

                    //Point centroid = agri.getCentroid();
                    //Coordinate cc = centroid.getCoordinate();
                    //System.out.println("Centroid Coordinate: " + cc.x  + " " + cc.y);
                    //System.out.println("Point Coordinate: " + coord.x  + " " + coord.y);

                    resultingArea = resultingArea.difference(agri);

                    intersection = circle.intersection(agri);
                    if(intersection.getArea() > 0)
                    {
                        //if(sim.extendedOutput)System.out.println("intersection area: " + intersection.getArea());
                    }
                }
            }

            Geometry lastGeom = fact.createGeometry(resultingArea);

            if(sim.landCoverIrrigationChannels)
            {
                bag = sim.irrigationChannelsPolygon.getGeometries();

                Boolean contained = false;
                int containedAll = 0;

                for(int i = 0; i < bag.size(); i++)
                {
                    MasonGeometry mg = (MasonGeometry)bag.get(i);
                    Geometry chann = (Geometry)mg.getGeometry(); 

                    if(chann.contains((Geometry)cPoint))
                    {
                        resultingArea = resultingArea.intersection(chann);
                        if(resultingArea.getArea() < lastGeom.getArea())
                        {
                            //System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
                            //System.out.println("resultingArea Area: " + resultingArea.getArea() + " circle area: " + lastGeom.getArea());

                            Geometry tmp = lastGeom.difference(resultingArea);
                            //System.out.println("difference lastGeom resultingArea: " + tmp.getArea());
                            //System.exit(0);
                        }

                        containedAll++;
                        contained = true;
                        //break;
                    }

                    //Point centroid = chann.getCentroid();
                    //Coordinate cc = centroid.getCoordinate();
                    //System.out.println("Centroid Coordinate: " + cc.x  + " " + cc.y);
                    //System.out.println("Point Coordinate: " + coord.x  + " " + coord.y);


                }

                //System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                //System.out.println("resultingArea Area: " + resultingArea.getArea() + " circle area: " + lastGeom.getArea());
                Geometry tmp = lastGeom.difference(resultingArea);
                //System.out.println("difference lastGeom resultingArea: " + tmp.getArea());
                if(containedAll == bag.size())
                {
                    resultingArea = fact.createGeometry(lastGeom);
                    //System.out.println("Contained all FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
                    //System.exit(0);
                }
                else
                {
                    //Patch!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    //System.out.println("patch ghgggggggggggggggggggggggg");
                    if(tmp.getArea() > 10)
                    {
                        //System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                        //System.out.println("aaaaaaaaaaaaaaaaaaaaaaPatch!!!!: " + tmp.getArea());
                        //resultingArea = lastGeom.difference(resultingArea);
                        //resultingArea = lastGeom.difference(resultingArea);
                        //System.exit(0);
                    }
                    else 
                    {
                        //System.out.println("tmp area < 10 " + tmp.getArea());
                        resultingArea = fact.createGeometry(lastGeom);
                    }
                }

                if(!contained)
                {
                    System.out.println("Pig household not contained in irrigation channels polygons");
                    household.printResume();
                    printResume();
                    //System.exit(0);
                }
            }



            double area = resultingArea.getArea();
            if(area >= homeRangeArea)doLoop = false;

            if(printOut)if(sim.extendedOutput)System.out.println("Circle range: " + range + " resultingArea area: " + area);

            stats++;
            if(stats == numMaxCircles)doLoop = false;
        }

        if(stats >= numMaxCircles)
        {
            System.out.println("Pig homeAreaShape did not converged");
            //System.exit(0);
        }


        if(printOut)if(sim.extendedOutput)System.out.println("");
        if(printOut)if(sim.extendedOutput)System.out.println("Final area: " + resultingArea.getArea());

        //circle = sim.utils.createCircle(coord, (2.0 * homeRange), 32);

        homeAreaShape = resultingArea;
        //homeAreaShape = (Geometry)circle;

    }





}//end of file ----------------


