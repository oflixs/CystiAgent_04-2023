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

import sim.util.geo.MasonGeometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Geometry;

import java.io.*;

//----------------------------------------------------
public class Human implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public Stoppable stopper;

    public Household household = null;
    public Household getHousehold(){return household;};
    public void setHousehold(Household y){household = y;};

    //public CoverPixel cpPosition = null;
    //public void setCpPosition(CoverPixel mg){cpPosition = mg;};
    //public CoverPixel getCpPosition(){return cpPosition;};

    //public CoverPixel cpPositionDefecationSite = null;

    public int identity = 0;

    int age = 0;

    public Boolean dead = false;

    //Parameters from CystiAgents NetLogo model
    public Boolean latrineUser    = false;
    public Boolean sewerUser    = false;
    public Boolean inTheBorder  = false;
    public double adherenceToLatrineBorderEffect = 1.0;
    public Boolean tapeworm    = false;
    public Boolean tapewormMature    = false;
    public Boolean traveling    = false;
    public Boolean strangerTraveler    = false;
    public Boolean traveler   = false;
    public int travelDuration    = 0;
    public int timeToTheNextTravel  = 0;
    public int timeSinceInfection    = 0;
    public int infectionDuration    = 0;
    public Boolean eligibleH    = false;
    public int infectDIA    = 0;//to infect the human when the Deterministic 
    public Boolean demoData    = false;//to infect the human when the Deterministic 
    //individual allocation method is used

    //Human defecation site
    public DefecationSite defecationSite = null;

    public int numWeekSteps = 0;

    public Boolean weeklyMeatPortion = false;

    public int weeklyMeatPortionCounter = 0;

    public String gender = "";//can be female or male

    public String education = "";//can be several education levels

    public String famRelation = "";//can be father, mather, child, grandparent, 
    //uncle, cousin, nephew, grandchild

    //GATES variables
    public Boolean studyParticipation = false;
    public Boolean acceptedNMTreatment = false;
    public String GATESID = "";

    //-------------------------------------------------
    //cystiHumans parameters   ------------------------
    //int lifespan = 0;
    Bag cysts = new Bag();
    public Boolean cook = false;//specify if the human agent is the household cook
    public String epiStatus = ""; // epilepsy status: active, inactive, asymptomatic
    public boolean ichHum; // ICH or hydrocephalus status of human
    //public boolean everNCCdisa; // GBPIAE
    public boolean lowRiskImmigrant; // GBPIAE
    public Integer reborn; // GBPIAE
    public Integer immigrated; // GBPIAE
    public Integer numberInPicture; // GBPIAE
    public Integer emigratedSince; // GBPIAE
    //public Integer currentStepCystBagSize; // GBPIAE
    //public Integer priorStepCystBagSize; // GBPIAE

    public String epiTreat; //epilepsy treatment: current, past or never
    public boolean epiTreatSuccess; // Epilepsy treatment success
    public String ichTreatment; // can be:  no, non-surgical (never surgery), surgical (can include non-surgical treatment also)
    public int ichTreatDelay; // ICH or hydrocephalus treatment delay, -1 if there is no treatment, positive integer otherwise

    //-------------------------------------------------
    //demo module parameters   ------------------------
    public boolean emigrated = false; // If the human emigrated it is no more in the village

    //R01 interventions params
    Boolean markedForTreat = false;
    int treatTimer = 0;
    Boolean markedForRetreatment = true;
    int reTreatTimer = 0;

    public Boolean joinedFromOtherVillage = false;

    //====================================================
    public Human(SimState pstate, Household ph, int pinfDIA, Boolean pdemoData, Boolean pstrangerT, Boolean defSite)
    {
        state = pstate;
        sim = (CystiAgents)state;

        //infectDIA = 0 will be infected with baseline prev prob
        //infectDIA = 1 will be infected 
        //infectDIA != 0 or 1 will be no infected 
        infectDIA  = pinfDIA;
        demoData = pdemoData;

        household = ph;

        //cpPosition = household.cpPosition;

        strangerTraveler = pstrangerT;

        //if(cpPosition == null)
        //{
        //    System.out.println ("Human cp null in Human init");
        //    System.exit(0);
        //}

        //sim.humanGrid.setObjectLocation(this, 
        //        sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[0],
        //        sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[1]
        //        );

        double interval = 1.0;
        this.stopper = sim.schedule.scheduleRepeating(this, 10, interval);

        if(!demoData)
        {
            age = (int)Math.round(sim.weeksInAYear * state.random.nextInt(85));
            identity = sim.humansIds;
            sim.humansIds++;
        }

        household.addHuman(this);

        sim.humansBag.add(this);

        //place the human defecation site around its  household
        if(defSite)
        {
            if(!sim.useLandCover)getDefecationSite();
            else getDefecationSiteLandCover();
        }

        //sim.baselineTnPrev = 0.02253;
        //0.02253 is the aerage human taeniasis prev of the entire TTEMP dataset
        //if(strangerTraveler)sim.baselineTnPrev = 1.0;
        if(infectDIA == 0 && state.random.nextDouble() < sim.baselineTnPrev)
        {
            //System.out.println ("InfectHuman baseline " + sim.humansIds);
            infectHumanBaseline();
        }
        //sim.baselineTnPrev = 0.0225;
        
        if(infectDIA == 1)infectHumanBaseline();


        //init cystihuman human
        if(sim.cystiHumans)sim.humanCH.initHumanCystiHuman(this);

        if(household.latrineUsers)latrineUser = true;
        else 
        {
            latrineUser = false;
            //System.exit(0);
        }

        if(household.sewerUsers)sewerUser = true;
        else 
        {
            sewerUser = false;
            //System.exit(0);
        }

        if(household.inTheBorder)
        {
            inTheBorder = true;
            adherenceToLatrineBorderEffect = sim.adherenceToLatrineBorderEffect;
            //sewerUser = true;
        }
        else 
        {
            inTheBorder = false;
            adherenceToLatrineBorderEffect = 1.0;
            //sewerUser = true;
            //System.exit(0);
        }

        //demo module
        reborn = -1; // GBPIAE
        immigrated = -1; // GBPIAE
        lowRiskImmigrant = false; // GBPIAE
        numberInPicture = -1; // GBPIAE
        emigratedSince = -1; // GBPIAE


    }

    //====================================================
    public void step(SimState state)
    {
        if(dead)System.out.println("step of a dead human!!!!!!!!!!!!");

        //if(!latrineUser)
        //{
        //    System.out.println ("no latrine user");
        //    household.printResume();
        //    printResume();
        //    System.exit(0);
        //}

        age++;
        numWeekSteps++;

        if(!emigrated)
        {
            weeklyMeatPortionCounter--;
            if(weeklyMeatPortionCounter == 0)weeklyMeatPortion = false;

            //if(dead)System.out.println("0step of a dead human!!!!!!!!!!!!");
            if(sim.travelerProp > 0.0)
            {
                //if(dead)System.out.println("1step of a dead human!!!!!!!!!!!!");
                if(strangerTraveler)
                {
                    travelDuration--;
                    //System.out.println("stranger traveler " + travelDuration);
                    if(travelDuration == 0)
                    {
                        returnHomeStrangerTraveler();
                        return;
                    }
                }
                //if(dead)System.out.println("2step of a dead human!!!!!!!!!!!!");

                //min age to be a traveler is 18 years
                if(traveler && age > 18 * sim.weeksInAYear)
                {
                    timeToTheNextTravel--; 
                    travelDuration--;
                    //System.out.println("travel starts");
                    if(traveling && travelDuration == 0)returnHomeTraveler();//if travel time elapsed 
                    if(!traveling && timeToTheNextTravel == 0)startTravel();
                    if(traveling)infectTravelers();
                }
            }

        }

        if(tapeworm)timeSinceInfection++;//this was advanceInfection of CystiAgents

        matureInfection();

        tapewormDie();

        //things to do if cystiHumans is active each human timestep
        if(sim.cystiHumans)sim.humanCH.humanCHStep(this);

        //demo module
        if(reborn > -1)reborn++; // GBPIAE
        if(immigrated > -1)immigrated++; // GBPIAE
        if(emigratedSince > -1)emigratedSince++; // GBPIAE

        //R01 interventions
        if(markedForTreat)
        {
            treatTimer--;
            if(treatTimer == 0)
            {
                //if(sim.extendedOutput)System.out.println("Human first treatment");
                markedForTreat = false;
                treat();
            }
        }

        if(markedForRetreatment)
        {
            reTreatTimer--;
            if(reTreatTimer == 0)
            {
                //if(sim.extendedOutput)System.out.println("Human second treatment after screening screening");
                markedForRetreatment = false;
                treat();
            }
        }

    }

    //===============================================
    public  void printResume()
    {
        if(!sim.extendedOutput)return;
        System.out.println("---- Human summary ----------------------");
        //System.out.println("Actual Comm name: " + sim.community.name);
        //System.out.println("Origin Comm name: " + simOrigin.community.name);
        System.out.println("Id: " + identity);

        System.out.println("Age: " + age);

        System.out.println("Household Id: " + household.simId);

        System.out.println("Village: " + sim.villageName); // GBPIAE

        if(latrineUser == true)System.out.println("The human uses the latrine");
        else System.out.println("The human does not use the latrine"); // GBPIAE

        if(sewerUser == true)System.out.println("The human uses the sewer");
        else System.out.println("The human does not use the sewer"); // GBPIAE

        if(latrineUser == true)System.out.println("The human live in a household far from the village center");
        else System.out.println("The human does notlive in a household far from the village center"); // GBPIAE

        if(tapeworm == true)System.out.println("The human is a tapeworm carrier");
        else System.out.println("The human is not a tapeworm carrier"); // GBPIAE

        if(tapewormMature == true)System.out.println("The human has a mature tapeworm"); // GBPIAE
        else System.out.println("The human does not carry a mature tapeworm"); // GBPIAE


        if(!emigrated){ // GBPIAE
            if(!(defecationSite.eggs || defecationSite.proglottid))System.out.println("Human defecation site not contaminated");
            if(defecationSite.eggs)System.out.println("Human defecation site contaminated with eggs");
            if(defecationSite.proglottid)System.out.println("Human defecation site contaminated with proglottids");
        }

        if(traveler == true)System.out.println("traveler");
        else System.out.println("not a traveler");

        if(strangerTraveler == true)System.out.println("strangerTraveler");
        else System.out.println("not a strangerTraveler");

        if(sim.cystiHumans) // GBPIAE
        { // GBPIAE
          if(cook == true)System.out.println("the human is a cook"); // GBPIAE
          else System.out.println("the human is not a cook"); // GBPIAE
          System.out.println("the human's epilepsy status is " + epiStatus); // GBPIAE
          if(epiStatus.equals("active") || epiStatus.equals("inactive")) // GB11mars
          { // GB11mars
              Human h = (Human)sim.humansBag.get(identity); // GB11mars
              System.out.println("The human's first seizure took place " + sim.humanCH.humanGetTimeSinceFirstSeizure(h) + " ago"); // GB11mars
              System.out.println("The human's latest seizure took place " + sim.humanCH.humanGetTimeSinceLastSeizure(h) + " ago"); // GB11mars
          }; // GB11mars

          System.out.println("the human's epilepsy treatment status is " + epiTreat); // GBPIAE
          if(epiTreatSuccess==true)System.out.println("epilepsy treatment, if applied, will be successful"); // GBPIAE
          else System.out.println("epilepsy treatment, if applied, will fail"); // GBPIAE
          if(ichHum==true)System.out.println("The human has ICH or hydrocephalus"); // GBPIAE
          else System.out.println("The human does not have ICH or hydrocephalus"); // GBPIAE
          System.out.println("the human's ICH treatment status is " + ichTreatment); // GBPIAE
          System.out.println("Delay for ICH treatment is set at " + ichTreatDelay); // GBPIAE

          if(reborn==-1)System.out.println("The human was not born during the simulation"); // GBPIAE
          else System.out.println("The human was born " + reborn + "weeks ago"); // GBPIAE

          if(immigrated==-1)System.out.println("The human is not an immigrant"); // GBPIAE
          else {System.out.println("The human immigrated " + immigrated + "weeks ago"); // GBPIAE
          System.out.println("The immigrants's number in the village picture is " + numberInPicture);} // GBPIAE

          if(lowRiskImmigrant)System.out.println("That individual was a low risk immigrant"); // GBPIAE
          else if(immigrated >-1 && !lowRiskImmigrant)System.out.println("That individual was a high risk immigrant"); // GBPIAE

        //  if(emigratedSince==-1)System.out.println("The human did not emigrate"); // GBPIAE
        //  else System.out.println("The human emigrated " + emigratedSince + "weeks ago"); // GBPIAE


          if(cysts.size()>0)System.out.println("The human has or has already had NCC"); // GBPIAE
          else System.out.println("The human does not have nor has ever had NCC"); // GBPIAE
          System.out.println("Today, the human has " + cysts.size() + "cysts, immature, mature, calcified or disappeared"); // GBPIAE

          for(int i = 0; i < cysts.size(); i++) // GB10mars
          {  
              HumanCyst hc = (HumanCyst)cysts.get(i); /// GB10mars
              hc.printResume(); // GB10mars
          } // GB10mars
          // System.out.println("One week ago, the human had " + priorStepCystBagSize + "cysts, immature, mature, calcified or disappeared"); // GBPIAE
        } // GBPIAE

        System.out.println("---- Human summary end -------------------");
    }

    //===============================================
    public  void infectHumanBaseline()
    {
        //System.out.println("infect human baseline ---------------");
        tapeworm = true;
        household.nTapeworms++;
        //if(!sim.tapewormCarriers.contains(this))sim.tapewormCarriers.add(this);
        //if(!latrineUser && !sim.tapewormCarriersNoLatrine.contains(this))sim.tapewormCarriersNoLatrine.add(this);

        //cpPosition.hInfected = true;

        double random = (sim.tnLifespanMean + state.random.nextGaussian() * sim.tnLifespanSd);
        //System.out.println("tnLifespan mean, sd,  number " + sim.tnLifespanMean + " " + sim.tnLifespanSd + " " + random);
        if(random <= 2)infectionDuration = 15;
        else infectionDuration = (int)Math.round(random);

        random = state.random.nextDouble() * infectionDuration;
        timeSinceInfection = infectionDuration - (int)Math.round(random);

        if(timeSinceInfection >= infectionDuration)timeSinceInfection = (int)Math.round(infectionDuration * 0.5);

        if(strangerTraveler)
        {
            //System.out.println(infectionDuration);
            //infectionDuration = 1500;
            timeSinceInfection = sim.tnIncubation;
            matureTn(true);
            //System.out.println("mature infection stranger");
        }
        
        if(timeSinceInfection >= sim.tnIncubation)matureTn(true);


       // System.out.println(infectionDuration + " " + timeSinceInfection);

    }

    //===============================================
    public  void startTravel()
    {
        //System.out.println("---- Human travel ----------------------");
        traveling = true;
        //System.exit(0);

        changeDefecationSiteState(false);

        //  exponential distribution
        double random = state.random.nextDouble();
        //System.out.println(sim.travelDuration);
        //System.out.println(Math.log(random));
        //System.out.println(- sim.travelDuration * Math.log(random));

        travelDuration = (int)Math.ceil(-sim.travelDuration * Math.log(random));
        //System.out.println("travel duration: " +travelDuration);
        if(travelDuration < 1)travelDuration = 2;

        double sa = Math.abs(sim.travelFreq + state.random.nextGaussian() * sim.travelFreq * 0.25);
        timeToTheNextTravel  = (int)Math.round(sa);
        //System.out.println("time to next travel: " + timeToTheNextTravel);

        if(timeToTheNextTravel <= travelDuration)timeToTheNextTravel = 2 * travelDuration + 1;
        //System.out.println("Human travelDuration and timeToTheNextTravel: " + travelDuration + " " + timeToTheNextTravel);
        //System.out.println("Both positives? and travelDuration < timetothenexttravel");

        //cpPosition = sim.utils.getCoverPixelFromCoords(state, 0, 0, "geo");

        //household.removeHuman(this);
        /*
        System.out.println("--START TRAVEL------------");
        System.out.println("human: " + identity + " start travel");
        System.out.println("matureTN: " + tapewormMature);
        System.out.println("defecSite  prglot: " + defecationSite.proglottid);
        System.out.println("defecSite  eggs: " + defecationSite.eggs);
        System.out.println("Human travelDuration and timeToTheNextTravel: " + travelDuration + " " + timeToTheNextTravel);
        */


        //System.out.println("---- Human travel end ------------------");
    }

    //===============================================
    public  void returnHomeStrangerTraveler()
    {
        //System.out.println("---- Human return home stranger traveler ");

        if(!sewerUser)
        {
            if(this.latrineUser)sim.numActiveDefecationSites = sim.numActiveDefecationSites - (1 - (sim.adherenceToLatrineUse * adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
            else sim.numActiveDefecationSites = sim.numActiveDefecationSites - sim.propDefInsideRadius;
        }

        //household.addHuman(this);

        removeFromPigsDefecatorsList();

        traveling = false;

        sim.strangerTravelersBag.remove(this);

        //pitxi
        //defecationSite.eggsAlive = false;
        //defecationSite.eggs = false;
        //defecationSite.proglottid = false;

        die();

        return;

    }

    //====================================================
    public void removeFromPigsDefecatorsList()
    {
         for(int i = 0; i < sim.pigsBag.size(); i++)
         {
             Pig pig = (Pig)sim.pigsBag.get(i);

             if(pig.defecatorsInRangeList.contains(this))
             {
                 if(!sewerUser)
                 {
                     if(this.latrineUser)pig.numDefecationSitesInHomeRange 
                         = pig.numDefecationSitesInHomeRange - (1 - (sim.adherenceToLatrineUse * adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                     else pig.numDefecationSitesInHomeRange = pig.numDefecationSitesInHomeRange - sim.propDefInsideRadius;
                 }
                 pig.defecatorsInRangeList.remove(this);
             }
         }
    }

    //===============================================
    public  void returnHomeTraveler()
    {
        //System.out.println("---- Human return home -----------------");
        traveling = false;
        //cpPosition = household.cpPosition;

        //household.addHuman(this);

        changeDefecationSiteState(true);

        /*
        System.out.println("--return home traveler ------------------------");
        System.out.println("human: " + identity + " return travel");
        System.out.println("matureTN: " + tapewormMature);
        System.out.println("defecSite  prglot: " + defecationSite.proglottid);
        System.out.println("defecSite  eggs: " + defecationSite.eggs);
        System.out.println("Human travelDuration and timeToTheNextTravel: " + travelDuration + " " + timeToTheNextTravel);
        */


    }

    //====================================================
    public void infectTravelers()
    {
        double random = state.random.nextDouble();
        if(random < sim.travelIncidence)
        {
            //System.out.println("---- Humans infectHuman traveler --- " + identity);
            infectHuman();
        }
    }

    //====================================================
    public void infectHuman()
    {
        if(tapeworm)return;
        //System.out.println("---- Humans infectHuman not traveler ---");
        tapeworm = true;
        household.nTapeworms++;

        double random = (sim.tnLifespanMean + state.random.nextGaussian() * sim.tnLifespanSd);
        //System.out.println("tnLifespan mean, sd,  number " + sim.tnLifespanMean + " " + sim.tnLifespanSd + " " + random);
        if(random <= 2)infectionDuration = 15;
        else infectionDuration = (int)Math.round(random);

        timeSinceInfection = 0;

        //if(!traveling)cpPosition.hInfected = true;

        //if(tapeworm && identity == 140)
        //{
        //    System.out.println("---- Humans infectHuman not traveler ---");
        //    System.out.println("infectionDuration: " + infectionDuration);
        //    System.out.println("timeSinceInfection: " + timeSinceInfection);

        //}
    }

    //====================================================
    public void matureInfection()
    {
        if(!tapeworm || tapewormMature)return;

        if(timeSinceInfection == sim.tnIncubation)
        {
            //System.out.println("---- mature infection human ");
            matureTn(true);
        }
    }

    //====================================================
    public void tapewormDie()
    {
        if(!tapeworm)return;

        if(timeSinceInfection == infectionDuration)
        {
            //System.out.println("Tepeworm die");
            matureTn(false);
        }
    }

    //====================================================
    public void treat()
    {
        if(!tapeworm)return;

        //System.out.println("---- Human treated!!!!!!!!!!!!!!!!!!!!!!");
        //if(strangerTraveler)System.out.println("---- Human strangerTraveler");
        matureTn(false);
    }

    //===============================================
    public  void die()
    {
        //if(1 == 1)return;
        //System.out.println("---- Human dies adios!");

        //printResume();
        //household.printResume();

        //CystiHuman remove the human cysts
        if(sim.cystiHumans)
        {
            int size = cysts.size();
            for(int i = 0; i < size; i++)
            {
                HumanCyst cyst = (HumanCyst)cysts.get(0);
                cyst.die();
            }
            cysts = new Bag();
        }

        matureTn(false);

        if(!emigrated)
        {
            household.removeHuman(this);
            sim.humansBag.remove(this);
        }

        if(emigrated)sim.emigrantsBag.remove(this);

        if(this.defecationSite != null)
        {
            changeDefecationSiteState(false);
            this.defecationSite.switchToSetToDie();
        }

        this.stopper.stop();

        dead = true;
        //System.out.println("---- Human dies finished!");

        return;
    }

    //===============HouseholdsSameLoc
    public void getDefecationSite()
    {
        Boolean printOut = false;
        //System.out.println(sim.village.name + " ---- get human defecation site Around Household");

        double contRadius = household.contRadiusHh;
        //double radius = contRadius/sim.geoCellSize;

        //System.out.println("Defecation position radius in pixels = " + radius);

        //IntBag xPos = new IntBag();
        //IntBag yPos = new IntBag();

        //System.out.println("xPos size: " + xPos.size());

        //cpPosition.printResume();
        //int x = sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[0];
        //int y = sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[1];
        //if(printOut)System.out.println("centrer coords = " + x + " " + y);
        //System.out.println("centrer coords = " + x + " " + y);
        //System.out.println(cpPosition.getXcor() + " " + cpPosition.getYcor());
        //System.out.println(household.cpPosition.getXcor() + " " + householdcpPosition.cpPosition.getYcor());

        //radius = 2;

        //sim.geoGrid.getRadialLocations(x, y, radius, sim.geoGrid.BOUNDED, true, sim.geoGrid.CENTER, true, xPos, yPos); 

        //System.out.println("xPos size: " + xPos.size());
        //System.out.println("yPos size: " + yPos.size());

        //int rand = state.random.nextInt(xPos.size());

        //cpPositionDefecationSite = (CoverPixel)sim.geoGrid.get(xPos.get(rand), yPos.get(rand)); 

        //defecationSite = new DefecationSite(state, cpPositionDefecationSite, this);

        //for(int i = 0; i < xPos.size(); i++)
        //{
        //    System.out.println("xPos, yPos, i: " + xPos.get(i) + ", " + yPos.get(i));
        //}

        //System.exit(0);

        if(printOut)System.out.println("---- Alternate method ----");

        double teta = 2 * Math.PI * state.random.nextDouble();


        //double rx = radius * Math.cos(teta);
        //double ry = radius * Math.sin(teta);

        //int xDef = (int)Math.round(rx + x);
        //int yDef = (int)Math.round(ry + y);

        //to get the pointPos of the def site
        Point centerPoint = household.geoPoint;
        Coordinate coord = centerPoint.getCoordinate();

        double xp = coord.x;
        double yp = coord.y;

        double rBuffer = 3;

        if(contRadius > rBuffer)
        {
            contRadius = (contRadius - rBuffer) * state.random.nextDouble();
            contRadius = contRadius + rBuffer;
        }

        double rrx = contRadius * Math.cos(teta);
        double rry = contRadius * Math.sin(teta);

        Coordinate newCoord = new Coordinate((xp + rrx), (yp + rry));

        GeometryFactory fact =  new GeometryFactory();
        Point defPoint = fact.createPoint(newCoord);

        //if(printOut)System.out.println("radius: " + radius);
        if(printOut)System.out.println("teta: " + teta);
        if(printOut)System.out.println("cos(teta), sin(teta): " + Math.cos(teta) + " " + Math.sin(teta));
        //if(printOut)System.out.println("rx, ry: " + rx + " " + ry);
        //if(printOut)System.out.println("xDef, yDef: " + xDef + " " + yDef);
        //if(printOut)System.out.println("geoGridwidth, geoGridHeight: " + sim.geoGrid.getWidth() + " " + sim.geoGrid.getHeight());

        //cpPositionDefecationSite = (CoverPixel)sim.geoGrid.get(xDef, yDef); 
        defecationSite = new DefecationSite(state, this);

        defecationSite.pointPos = defPoint;

    }

    //===============================================
    public void getDefecationSiteLandCover()
    {
        Boolean printOut = false;
        //System.out.println(sim.village.name + " ---- get human defecation site Around Household");

        double contRadius = household.contRadiusHh;
        //double radius = contRadius/sim.geoCellSize;

        //System.out.println("Defecation position radius in pixels = " + radius);

        //IntBag xPos = new IntBag();
        //IntBag yPos = new IntBag();

        //System.out.println("xPos size: " + xPos.size());

        //cpPosition.printResume();
        //int x = sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[0];
        //int y = sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[1];
        //if(printOut)System.out.println("centrer coords = " + x + " " + y);
        //System.out.println("centrer coords = " + x + " " + y);
        //System.out.println(cpPosition.getXcor() + " " + cpPosition.getYcor());
        //System.out.println(household.cpPosition.getXcor() + " " + householdcpPosition.cpPosition.getYcor());

        //radius = 2;

        //sim.geoGrid.getRadialLocations(x, y, radius, sim.geoGrid.BOUNDED, true, sim.geoGrid.CENTER, true, xPos, yPos); 

        //System.out.println("xPos size: " + xPos.size());
        //System.out.println("yPos size: " + yPos.size());

        //int rand = state.random.nextInt(xPos.size());

        //cpPositionDefecationSite = (CoverPixel)sim.geoGrid.get(xPos.get(rand), yPos.get(rand)); 

        //defecationSite = new DefecationSite(state, cpPositionDefecationSite, this);

        //for(int i = 0; i < xPos.size(); i++)
        //{
        //    System.out.println("xPos, yPos, i: " + xPos.get(i) + ", " + yPos.get(i));
        //}

        //System.exit(0);

        if(!sim.landCoverIrrigationChannels)
        {
            if(printOut)System.out.println("---- Alternate method ----");

            double teta = 2 * Math.PI * state.random.nextDouble();

            //double rx = radius * Math.cos(teta);
            //double ry = radius * Math.sin(teta);

            //int xDef = (int)Math.round(rx + x);
            //int yDef = (int)Math.round(ry + y);

            //generate the def site point
            //pitxi here the contRadius have to be between the cont max and a minimum of....?
            Point centerPoint = household.geoPoint;
            Coordinate coord = centerPoint.getCoordinate();

            double xp = coord.x;
            double yp = coord.y;

            double rrx = contRadius * Math.cos(teta);
            double rry = contRadius * Math.sin(teta);

            Coordinate newCoord = new Coordinate((xp + rrx), (yp + rry));

            GeometryFactory fact =  new GeometryFactory();
            Point defPoint = fact.createPoint(newCoord);

            //if(printOut)System.out.println("radius: " + radius);
            if(printOut)System.out.println("teta: " + teta);
            if(printOut)System.out.println("cos(teta), sin(teta): " + Math.cos(teta) + " " + Math.sin(teta));
            //if(printOut)System.out.println("rx, ry: " + rx + " " + ry);
            //if(printOut)System.out.println("xDef, yDef: " + xDef + " " + yDef);
            //if(printOut)System.out.println("geoGridwidth, geoGridHeight: " + sim.geoGrid.getWidth() + " " + sim.geoGrid.getHeight());

            //cpPositionDefecationSite = (CoverPixel)sim.geoGrid.get(xDef, yDef); 
            defecationSite = new DefecationSite(state, this);

            defecationSite.pointPos = defPoint;
        }
        else
        {
            if(printOut)System.out.println("---- Alternate method ----");

            Point centerPoint = household.geoPoint;
            Coordinate coord = centerPoint.getCoordinate();

            Bag bag = sim.irrigationChannelsPolygon.getGeometries();

            Boolean contained = false;
            int containedAll = 0;
            Geometry chann = null;

            for(int i = 0; i < bag.size(); i++)
            {
                MasonGeometry mg = (MasonGeometry)bag.get(i);
                chann = (Geometry)mg.getGeometry(); 

                if(chann.contains((Geometry)centerPoint))
                {
                    break;
                }
            }


            Boolean goOn = true;

            //int xDef = 0;
            //int yDef = 0;

            Point defPoint = null;

            while(goOn)
            {
                //radius = radius * state.random.nextDouble();
                double teta = 2 * Math.PI * state.random.nextDouble();

                //double rx = radius * Math.cos(teta);
                //double ry = radius * Math.sin(teta);

                //xDef = (int)Math.round(rx + x);
                //yDef = (int)Math.round(ry + y);

                //generate the def site point
                //pitxi here the contRadius have to be between the cont max and a minimum of....?

                double xp = coord.x;
                double yp = coord.y;

                double rrx = contRadius * Math.cos(teta);
                double rry = contRadius * Math.sin(teta);

                Coordinate newCoord = new Coordinate((xp + rrx), (yp + rry));

                GeometryFactory fact =  new GeometryFactory();
                defPoint = fact.createPoint(newCoord);

                if(chann.contains((Geometry)defPoint))goOn = false;

                //if(printOut)System.out.println("radius: " + radius);
                if(printOut)System.out.println("teta: " + teta);
                if(printOut)System.out.println("cos(teta), sin(teta): " + Math.cos(teta) + " " + Math.sin(teta));
                //if(printOut)System.out.println("rx, ry: " + rx + " " + ry);
                //if(printOut)System.out.println("xDef, yDef: " + xDef + " " + yDef);
                //if(printOut)System.out.println("geoGridwidth, geoGridHeight: " + sim.geoGrid.getWidth() + " " + sim.geoGrid.getHeight());



            }

            //cpPositionDefecationSite = (CoverPixel)sim.geoGrid.get(xDef, yDef); 
            defecationSite = new DefecationSite(state, this);

            defecationSite.pointPos = defPoint;



        }

    }



    //===============================================
    //==mature human tepeworm (true) or recover the human (false)
    public void matureTn(Boolean mature)
    {
        if(mature)
        {
            //System.out.println("human: " + identity + " matured tn");
            tapewormMature = true;
            if(!traveling && !emigrated)
            {
                defecationSite.proglottid = true;
                defecationSite.eggs = true;
                defecationSite.eggsAlive = false;

                if(!sim.tapewormCarriers.contains(this))sim.tapewormCarriers.add(this);
                if(!latrineUser && !sewerUser && !sim.tapewormCarriersNoLatrine.contains(this))sim.tapewormCarriersNoLatrine.add(this);
            }
        }
        else //Here the tapeworm dies and the human recovers
        {
            //System.out.println("human: " + identity + " unmatured tn");
            if(tapewormMature && !emigrated)
            {
                defecationSite.eggsAlive = true;
                defecationSite.eggs = true;
            }

            tapewormMature = false;

            tapeworm = false;
            timeSinceInfection = 0;
            infectionDuration = 0;

            //cpPosition.hInfected = false;

            if(!emigrated)defecationSite.proglottid = false;

            if(sim.tapewormCarriers.contains(this))
            {
                sim.tapewormCarriers.remove(this);
            }

            if(!latrineUser && !sewerUser && sim.tapewormCarriersNoLatrine.contains(this))
            {
                sim.tapewormCarriersNoLatrine.remove(this);
            }

        }

    }

    //===============================================
    //==switch on (true) and off (false) thr proglottid inthe human defec site
    public void changeDefecationSiteState(Boolean dState)
    {
        if(dState)//on state the human is not traveling
        {
            if(tapewormMature)
            {
                defecationSite.proglottid = true;
                defecationSite.eggs = true;
                defecationSite.eggsAlive = false;

                //cpPosition.hInfected = true;

                if(!sim.tapewormCarriers.contains(this))sim.tapewormCarriers.add(this);
                if(!latrineUser && !sewerUser && !sim.tapewormCarriersNoLatrine.contains(this))sim.tapewormCarriersNoLatrine.add(this);
            }
        }
        else //off state the human is trasveling
        {
            defecationSite.proglottid = false;
            if(tapewormMature)
            {
                defecationSite.eggsAlive = true;
                defecationSite.eggs = true;
            }

            //cpPosition.hInfected = false;

            if(sim.tapewormCarriers.contains(this))
            {
                sim.tapewormCarriers.remove(this);
            }
            if(!latrineUser && !sewerUser && sim.tapewormCarriersNoLatrine.contains(this))
            {
                sim.tapewormCarriersNoLatrine.remove(this);
            }
        }

    }

}
