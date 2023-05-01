package sim.app.cystiagents;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;

import java.util.ArrayList;
import java.util.List;

import java.util.*;

import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

//===============================================
public class Village extends Thread implements Steppable
{ 

    private final CystiAgents simVilla;
    //ThreadLocal<CystiAgents> threadLocalSim = new ThreadLocal<>();

    private final CystiAgentsWorld simW;
    private final long tStart;	

    private final String name;
    private final String group;
    private final String number;

    private final int id;
    public CyclicBarrier barrier;
    public int villageIndex = 0;

    public String intervention = "";

    //Interventions variables  ------------------------
    public String interventionType = "";
    public int R01InterventionArm = 0;

    private Boolean doStopSim = false;

    private Boolean mainJointVillage = false;
    public void setMainJointVillage(Boolean val){mainJointVillage = val;}

    private String joinedName = "";
    public void setJoinedName(String val){joinedName = val;}
    public String getJoinedName(){return joinedName;}

    private String determIndAllFileJoined = "";
    public String getDetermIndAllFileJoined(){return determIndAllFileJoined;};

    //Village paramenters 

    //===============================================
    public Village(long seed, int pj, CystiAgentsWorld syncObj, CyclicBarrier barrier)
    {
        simW = syncObj;

        id = simW.villageId;
        simW.villageId++;

        //seed = 100;
        this.simVilla = new CystiAgents(seed);
        //System.out.println ("seed: " + seed);
        //System.exit(0);

        this.villageIndex = pj;

        this.name = simW.villagesNames.get(villageIndex);
        //System.out.println ("village name: " + name);
        this.group = simW.villagesGroup.get(villageIndex);
        //System.out.println ("village group: " + group);
        this.number = simW.villagesNamesNumbers.get(villageIndex);
        //System.out.println ("village number: " + number);


        tStart = System.currentTimeMillis();

        //t = new Thread(this);

        simVilla.village = this;
        simVilla.simW = simW;
        simVilla.simName = simW.simName;
        simVilla.villageName = name;
        simVilla.villageNameNumber = number;
        simVilla.villageGroup = group;
        simVilla.worldInputFile = simW.worldInputFile;

        //System.out.println ("Village " + name + " created");
        //System.out.println ("From Village Village name: " + this.name);

        this.setName(name);
        this.barrier = barrier;
        //System.exit(0);

        //System.out.println ("Sim Village name: " + simVilla.villageName);
        //System.out.println ("Sim Village name thread: " + this.getName());
    }

    //===============================================
    public CystiAgents getsimVilla() 
    {
        return this.simVilla;
    }

    //===============================================
    @Override
    public  void run()
    {
        //This is just to make it crash
        //List pippo = new ArrayList<Double>();
        //double dp = (Double)pippo.get(0);

        long stepNum = 0;		
        int numStepAlone = simW.numStepSync; //steps are weeks
        //System.out.println ("1run village name: " + name);
        simVilla.start();


        if(doStopSim)
        {
            rmDirs();
            sleepForever();//to join the village togehter
            //System.exit(0);
        }

        if(simVilla.simW.allVillagesInOneSim && mainJointVillage)
        {
            simVilla.startAll.getDirsTreeMainJointVillage();
        }

        //System.out.println ("houses bag size from village: " + simVilla.householdsBag.size());
        //System.exit(0);

       simW.numVillaSteps = this.simVilla.numStep;

        Boolean loop = true;

        Boolean first = true;

        if(simVilla.extendedOutput)System.out.println(" ");
        if(simVilla.extendedOutput)System.out.println("--------------------------------------------------");
        if(simVilla.extendedOutput)System.out.println("==================================================");
        if(simVilla.extendedOutput)System.out.println(this.getName()  + " is launching the simulation");
        if(simVilla.extendedOutput)System.out.println("==================================================");
        if(simVilla.extendedOutput)System.out.println("--------------------------------------------------");

        //If the Deterministic Individual Allocation is used read or write
        //the humans, pigs and eggs population to file
        if(simVilla.readPopFromSurveyCensusFile && simVilla.deterministicIndividualsAllocation.equals("write"))
        {
            if(simVilla.extendedOutput)System.out.println(" ");
            if(simVilla.extendedOutput)System.out.println("--------------------------------------------------");
            if(simVilla.extendedOutput)System.out.println(name  + " is writing the populations state to village picture file");
            writeVillageToFile("all", false);
            if(simVilla.extendedOutput)System.out.println(name  + "  writing the populations state to village picture file done");
        }

        if(simVilla.simW.allVillagesInOneSim)
        {
            if(simVilla.deterministicIndividualsAllocation.equals("read"))
            {
                if(simVilla.extendedOutput)System.out.println ("Writing the village picture to file for joined village");

                String oldFile = "./outputs/DIAFiles/" + simVilla.simName + "_" + simVilla.villageNameNumber + "_DIA.txt";
                String newFile = "./outputs/DIAFiles/" + joinedName + "_Joined_DIA.txt";

                if(simVilla.extendedOutput)System.out.println ("New village file: " + newFile);

                simVilla.deterministicIndividualsAllocationFile = newFile;
                determIndAllFileJoined = newFile;

                writeVillageToFile("all", false);

                //simVilla.deterministicIndividualsAllocationFile = oldFile;
            }
        }

       
        //System.exit(0);

        //writeVillageToFile();
        //readVillageFromFile();
        //writeVillageToFile();

        //System.exit(0);

outWhile: 
        while(loop)
        {
            //try { 

                for(int i = 0; i < numStepAlone; i++)
                {
                    //System.out.println( this.getName() + " schedule get time: " + simVilla.schedule.getTime()  + " simVilla.numStep: " +  simVilla.numStep);
                    //System.exit(0);

                    stepNum++;

                    if(simVilla.schedule.getTime() < simVilla.numStep - 2)
                    {

                        //long t1 = System.nanoTime();
                        long t1 = System.currentTimeMillis();

                        simVilla.schedule.step(simVilla);

                        long t2 = System.currentTimeMillis();
                        simVilla.runTime = t2 - t1;

                        simVilla.totRunTime = simVilla.totRunTime + simVilla.runTime;
                        simVilla.statRunTime++;

                        //System.out.println(this.getName() + " "  + simVilla.nPrint + " " + stepNum);
                        if((stepNum % simVilla.nPrint) == 0)
                        {
                            //barrier.await(); 
                            //simW.numPrint = 0;
                            //barrier.await(); 
                            //printOutput();
                            if(simVilla.extendedOutput)
                            {
                                simVilla.statistics.calcStats();
                                //pitxi
                                //to write the to the standard output eery nPritn steps
                                simVilla.statistics.writeStandardOutput();
                            }
                        }
                    }
                    else
                    {
                        if(doStopSim)break outWhile; //this is to sto the sim when required
                        if(first)simW.numRunning--;
                        if(simW.numRunning <= 0)break outWhile;
                        first = false;
                    }
                }

                //barrier.await(); 
            //} 
            //catch (InterruptedException | BrokenBarrierException e) 
            //{ 
            //   e.printStackTrace(); 
            //}
        }

        if(simVilla.extendedOutput)System.out.println(this.getName() + " simulation ended");


        simW.numRunning--; 

    }

    //===============================================
    public void sleepForever()
    {
        try {
            this.sleep(Long.MAX_VALUE);
        }
        catch (Exception e) {
        }
    }
 



    /*
    //===============================================
    public void printOutput()
    {
        try{
            synchronized(this)
            {

                while (simW.numPrint < simW.numVilla)
                {
                    try {         
                        Thread.sleep(1);
                        //System.out.println("Num print: " + simW.numPrint);
                        //System.out.println("Id: " + id);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(simW.numPrint == id)
                    {

                        if(simVilla.extendedOutput)
                        {
                            if(simVilla.burnin)simVilla.statistics.calcStats();
                            //pitxi
                            //to write the to the standard output eery nPritn steps
                            simVilla.statistics.writeStandardOutput();
                        }
                        simW.numPrint++;

                        //System.out.println("Num print: " + simW.numPrint);
                        //System.out.println("Id: " + id);
                        //System.exit(0);

                        barrier.await(); 
                    }
                }


            }
        } 
        catch (InterruptedException | BrokenBarrierException e) 
        { 
            e.printStackTrace(); 
        }

    }
    */


    //===============================================
    public void stopSim()
    {
        if(simVilla.extendedOutput)System.out.println(this.getName() + " is changing parameters to stop sim"); 
        doStopSim = true;
        simVilla.numStep = 0;
        simW.numRunning--; 
    }

    //===============================================
    public void villaWait()
    {
        try { 
            if(simVilla.extendedOutput)System.out.println(this.getName() + " is calling await()"); 
            if(simVilla.extendedOutput)System.out.println(this.getName() + " num threads waiting: " + barrier.getNumberWaiting());
            barrier.await(); 
            if(simVilla.extendedOutput)System.out.println(this.getName() + " has started running again"); 
        } 
        catch (InterruptedException | BrokenBarrierException e) 
        { 
            e.printStackTrace(); 
        }
    }

    //===============================================
    public void init()
    {

        //System.out.println ("========Simulation Starts======================");        
        simVilla.inputFile = this.name;
    }

    //===============================================
    public void step(SimState state)
    {
        long dt;
        if((simVilla.schedule.getSteps() % 100 ) == 0)
        {              
            dt = System.currentTimeMillis() - this.tStart;

            if(simVilla.extendedOutput)System.out.println(" Com " + this.getName() + " Step "+ simVilla.schedule.getSteps() + " Step Time" + dt/simVilla.schedule.getSteps());  

        }
    }

    //===============================================
    public long getSimVillaSteps()
    {
        return simVilla.schedule.getSteps();
    }

    //====================================================
    public void readVillageFromFile()
    {
        Boolean printOut = false;

        String line = "";

        Bag tmpBag = null;

        tmpBag = new Bag(simVilla.humansBag);
        for(int i = 0; i < tmpBag.size(); i++)
        {
            Human h = (Human)tmpBag.get(i);
            h.die();
        }

        tmpBag = new Bag(simVilla.pigsBag);
        for(int i = 0; i < tmpBag.size(); i++)
        {
            Pig p = (Pig)tmpBag.get(i);
            p.die();
        }

        tmpBag = new Bag(simVilla.defecationSitesBag);
        for(int i = 0; i < tmpBag.size(); i++)
        {
            DefecationSite def = (DefecationSite)tmpBag.get(i);
            def.die();
        }
        
        if(simVilla.householdsBag.size() == 0)
        {
            simVilla.householdsGen.initEmptyHouseholds();
        }

        simVilla.humansBag = new Bag();
        simVilla.pigsBag = new Bag();
        //simVilla.defecationSitesBag = new Bag();

        File f = null;
        FileWriter w = null;

        int stats = 0;

        HashMap <String, String> householdList = new HashMap<String, String>();
        HashMap <String, String> humanList = new HashMap<String, String>();
        HashMap <String, String> pigList = new HashMap<String, String>();
        HashMap <String, String> seroDataList = new HashMap<String, String>();
        //HashMap <String, String> eggList = new HashMap<String, String>();
        String strLine = "";

        Boolean villageStart = false;
        Boolean humanStart = false;
        Boolean pigStart = false;
        Boolean householdStart = false;
        //Boolean eggStart = false;
        Boolean householdDataStart = false;
        Boolean humanDataStart = false;
        Boolean seroPrevData = false;

        Household hh = null;
        Human h = null;

        int numHh = 0;
        int numH = 0;
        int numP = 0;

        //To check the read
        try{
            //to read
            if(simVilla.extendedOutput)System.out.println (simVilla.villageName + " Village picture file: " + simVilla.deterministicIndividualsAllocationFile);
            FileInputStream fstream = new FileInputStream(simVilla.deterministicIndividualsAllocationFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   
            {
                // Print the content on the console
                //System.out.println (strLine);
                //strLine = strLine.trim();

                String delims = "[ ]+";
                String[] words = strLine.split(delims);

                if(words[0].equals("VillageAgentStart") && words[1].equals(name))
                {
                    villageStart = true;
                    continue;
                }
                if(villageStart && words[0].equals("VillageAgentEnd"))
                {
                    villageStart = false;
                    continue;
                }

                if(words[0].equals("R01serologyDataStart"))
                {
                    seroPrevData = true;
                    continue;
                }
                if(villageStart && words[0].equals("R01serologyDataEnd"))
                {
                    seroPrevData = false;
                    R01ReadObsDataFromFile(seroDataList);
                    seroDataList = new HashMap<String, String>();
                    continue;
                }
                if(seroPrevData)
                {
                    String tmp = "";
                    for(int i = 1; i < words.length; i++)
                    {
                        tmp = tmp + words[i];
                    }
                    if(printOut)System.out.println(words[0] + ": " + tmp);
                    seroDataList.put(words[0], tmp);
                }





                if(villageStart)
                {
                    //System.exit(0);
                    //if(printOut)System.out.println ("Read a household agent ------------------------");
                    if(words[0].equals("HouseholdAgentStart"))
                    {
                        if(printOut)System.out.println ("HouseStatrts");
                        //System.exit(0);
                        householdStart = true;
                        householdDataStart = true;
                        continue;
                    }
                    if(householdStart && words[0].equals("HouseholdAgentEnd"))
                    {
                        if(printOut)System.out.println ("House complete ends");
                        householdStart = false;
                        continue;
                    }

                    if(householdDataStart && words[0].equals("HouseholdAgentDataEnd"))
                    {
                        householdDataStart = false;
                        if(printOut)System.out.println ("House data End-------------");
                        hh = readHouseholdFromFile(householdList);
                        numHh++;
                        householdList = new HashMap<String, String>();
                        continue;
                    }
                    if(householdDataStart)
                    {
                        String tmp = "";
                        for(int i = 1; i < words.length; i++)
                        {
                            tmp = tmp + words[i];
                        }
                        if(printOut)System.out.println(words[0] + ": " + tmp);
                        householdList.put(words[0], tmp);
                    }

                    //Read a human agent ----------------------------
                    if(words[0].equals("HumanAgentStart"))
                    {
                        if(printOut)System.out.println ("Human Statrts");
                        humanStart = true;
                        humanDataStart = true;
                        continue;
                    }
                    if(humanStart && words[0].equals("HumanAgentEnd"))
                    {
                        if(printOut)System.out.println ("Human Statrts");
                        humanStart = false;
                        continue;
                    }
                    if(humanDataStart && words[0].equals("HumanAgentDataEnd"))
                    {
                        if(printOut)System.out.println ("Human data ends");
                        humanDataStart = false;
                        h = readHumanFromFile(humanList, hh);
                        numH++;
                        humanList = new HashMap<String, String>();
                        continue;
                    }

                    if(humanDataStart)
                    {
                        String tmp = "";
                        for(int i = 1; i < words.length; i++)
                        {
                            tmp = tmp + words[i];
                        }
                        humanList.put(words[0], tmp);
                    }


                    //Read a pig agent ----------------------------
                    if(words[0].equals("PigAgentStart"))
                    {
                        pigStart = true;
                        continue;
                    }
                    if(pigStart && words[0].equals("PigAgentEnd"))
                    {
                        pigStart = false;
                        readPigFromFile(pigList, hh);
                        numP++;
                        pigList = new HashMap<String, String>();
                        continue;
                    }

                    if(pigStart)
                    {
                        String tmp = "";
                        for(int i = 1; i < words.length; i++)
                        {
                            tmp = tmp + words[i];
                        }
                        pigList.put(words[0], tmp);

                    }


                    /*
                    //Read a egg agent ----------------------------
                    if(words[0].equals("EggAgentStart"))
                    {
                    eggStart = true;
                    continue;
                    }
                    if(eggStart && words[0].equals("EggAgentEnd"))
                    {
                    eggStart = false;
                    readEggFromFile(eggList, h);
                    eggList = new HashMap<String, String>();
                    continue;
                    }

                    if(eggStart)
                    {
                    String tmp = "";
                    for(int i = 1; i < words.length; i++)
                    {
                    tmp = tmp + words[i];
                    }
                    eggList.put(words[0], tmp);
                    }
                    */

                }//village ends


            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println ("Problem with Determ. Individuals Allocation File file");
            System.exit(0);
        }

        if(simVilla.extendedOutput)System.out.println ("Num household read from file: " + numHh);
        if(simVilla.extendedOutput)System.out.println ("Num humans read from file: " + numH);
        if(simVilla.extendedOutput)System.out.println ("Num pigs read from file: " + numP);

        SimState stateTmp = (SimState)simVilla;

        simVilla.householdsBag.shuffle(stateTmp.random);
        simVilla.humansBag.shuffle(stateTmp.random);
        simVilla.pigsBag.shuffle(stateTmp.random);

        //some additional setups to build the village
        simVilla.pigsGen.setBreedingSows();
        simVilla.householdsGen.createHousesNeighboursMap();

        if(simVilla.countDefecationSites == null)simVilla.countDefecationSites = new CounterDefecationSitesPigs(simVilla);
        simVilla.countDefecationSites.initDefecationSitesPigs();
        simVilla.pigsGen.countNumberDefecationSitesAroundPigs();

        simVilla.humansGen.getNumActiveDefecationSites();

        //Initialize cystiHumans humans and households 
        if(simVilla.cystiHumans)
        {
            simVilla.humanCH.selectCooks();
        }

       // System.exit(0);



    }

    //====================================================
    public Household readHouseholdFromFile(HashMap<String , String> list)
    {
        //System.out.println("The list: ");
        //for(String key : list.keySet())
        //{
        //    System.out.println(key + ": " + list.get(key));
        //}

        int simId = Integer.parseInt(list.get("simId"));
        //System.out.println("House simId: " + simId);

        Household hh = null;

        for(int i = 0; i < simVilla.householdsBag.size(); i++)
        {
            hh = (Household)simVilla.householdsBag.get(i);
            //System.out.println("House from list simId: " + hh.simId);
            if(hh.simId == simId)break;
        }
        
        if(hh == null)
        {
            System.out.println("House simId: " + simId + " not found");
            System.exit(0);
        }

        hh.trialId = Integer.parseInt(list.get("trialId"));

        hh.type = list.get("type");

        if(list.get("pigOwner").equals("true"))hh.pigOwner = true;
        else hh.pigOwner = false;

        hh.corralUse = list.get("corralUse");

        if(list.get("corral").equals("true"))hh.corral = true;
        else hh.corral = false;

        if(list.get("latrine").equals("true"))hh.latrine = true;
        else hh.latrine = false;

        if(list.get("latrineUsers").equals("true"))hh.latrineUsers = true;
        else hh.latrineUsers = false;

        if(list.get("sewerUsers").equals("true"))hh.sewerUsers = true;
        else hh.sewerUsers = false;

        if(list.get("sewerUsers").equals("true"))hh.sewerUsers = true;
        else hh.sewerUsers = false;

        if(list.get("travelerHh").equals("true"))hh.travelerHh = true;
        else hh.travelerHh = false;

        hh.contRadiusHh = Double.parseDouble(list.get("contRadiusHh"));

        hh.waterSuplyType = list.get("waterSuplyType");

        hh.numPigsCorraled = Integer.parseInt(list.get("numPigsCorraled"));
        hh.numPigsAmarrados = Integer.parseInt(list.get("numPigsAmarrados"));
        hh.numPigsFree = Integer.parseInt(list.get("numPigsFree"));

        hh.distClose = Double.parseDouble(list.get("distClose"));

        hh.targetNumOfPigs = Integer.parseInt(list.get("targetNumOfPigs"));

        //System.out.println("House contRadiusHh file: " + list.get("contRadiusHh"));
        //System.out.println("House contRadiusHh: " + hh.contRadiusHh);

        //System.exit(0);
        return hh;

    }

    //====================================================
    public Human readHumanFromFile(HashMap<String , String> list, Household hh)
    {
        Human h = new Human((SimState)simVilla, hh, 0, false, false, true);

        h.identity = Integer.parseInt(list.get("identity"));
        h.age = Integer.parseInt(list.get("age"));

        h.gender = list.get("gender");

        h.education = list.get("education");

        h.famRelation = list.get("famRelation");

        if(list.get("studyParticipation").equals("true"))h.studyParticipation = true;
        else h.studyParticipation = false;

        if(list.get("acceptedNMTreatment").equals("true"))h.acceptedNMTreatment = true;
        else h.acceptedNMTreatment = false;

        h.GATESID = list.get("GATESID");

        if(list.get("latrineUser").equals("true"))h.latrineUser = true;
        else h.latrineUser = false;

        if(list.get("sewerUser").equals("true"))h.sewerUser = true;
        else h.sewerUser = false;

        if(list.get("tapeworm").equals("true"))h.tapeworm = true;
        else h.tapeworm = false;

        if(list.get("traveling").equals("true"))h.traveling = true;
        else h.traveling = false;

        if(list.get("traveler").equals("true"))h.traveler = true;
        else h.traveler = false;

        h.travelDuration = Integer.parseInt(list.get("travelDuration"));
        h.timeToTheNextTravel = Integer.parseInt(list.get("timeToTheNextTravel"));
        h.timeSinceInfection = Integer.parseInt(list.get("timeSinceInfection"));
        h.infectionDuration = Integer.parseInt(list.get("infectionDuration"));

        if(list.get("tapewormMature").equals("true"))h.tapewormMature = true;
        else h.tapewormMature = false;

        if(list.get("eligibleH").equals("true"))h.eligibleH = true;
        else h.eligibleH = false;

        if(list.get("emigrated").equals("true"))h.emigrated = true;
        else h.emigrated = false;

        h.numWeekSteps = Integer.parseInt(list.get("numWeekSteps"));

        return h;

    }
    //====================================================
    public void readPigFromFile(HashMap<String , String> list, Household hh)
    {
        Pig p = new Pig((SimState)simVilla, hh, true, false);

        p.identity = Integer.parseInt(list.get("identity"));

        p.age = Integer.parseInt(list.get("age"));

        p.censusIdentity = Integer.parseInt(list.get("censusIdentity"));

        p.numCysts = Integer.parseInt(list.get("numCysts"));

        if(list.get("seropositive").equals("true"))p.seropositive = true;
        else p.seropositive = false;

        if(list.get("heavyInfected").equals("true"))p.heavyInfected = true;
        else p.heavyInfected = false;

        //if(list.get("markedForSlaughter").equals("true"))p.markedForSlaughter = true;
        //else p.markedForSlaughter = false;

        if(list.get("eligibleP").equals("true"))p.eligibleP = true;
        else p.eligibleP = false;

        if(list.get("treated").equals("true"))p.treated = true;
        else p.treated = false;

        p.treatmentProtectedTime = Double.parseDouble(list.get("treatmentProtectedTime"));

        p.stopSlaughterTimer = Double.parseDouble(list.get("stopSlaughterTimer"));

        p.vaccDose = Double.parseDouble(list.get("vaccDose"));

        if(list.get("vaccinated").equals("true"))p.vaccinated = true;
        else p.vaccinated = false;

        p.corraled = list.get("corraled");

        //p.slaughterAge = Integer.parseInt(list.get("slaughterAge"));

        p.homeRange = Double.parseDouble(list.get("homeRange"));

        p.homeRangeArea = Double.parseDouble(list.get("homeRangeArea"));

        if(list.get("dead").equals("true"))p.dead = true;
        else p.dead = false;

        if(list.get("isCorraled").equals("true"))p.isCorraled = true;
        else p.isCorraled = false;

        if(list.get("imported").equals("true"))p.imported = true;
        else p.imported = false;

        p.numDefecationSitesInHomeRange = Double.parseDouble(list.get("numDefecationSitesInHomeRange"));

        p.numSteps = Integer.parseInt(list.get("numSteps"));

        //cysts
        p.numDegeneratedCystsIc = Integer.parseInt(list.get("numDegeneratedCystsIc"));
        p.numDegeneratedCystsIcThisTimeStep = Integer.parseInt(list.get("numDegeneratedCystsIcThisTimeStep"));
        p.numDegeneratedCystsIi = Integer.parseInt(list.get("numDegeneratedCystsIi"));
        p.numCystsFromProglottids = Integer.parseInt(list.get("numCystsFromProglottids"));
        p.numCystsFromEggs = Integer.parseInt(list.get("numCystsFromEggs"));

        p.immunityO = Double.parseDouble(list.get("immunityO"));
        p.immunityC = Double.parseDouble(list.get("immunityC"));

        //the breeding sow will be reselect after the village picture reading
        //if(list.get("pregnant").equals("true"))p.pregnant = true;
        //else p.pregnant = false;
        //if(list.get("breedingSow").equals("true"))p.breedingSow = true;
        //else p.breedingSow = false;

        p.GATESID = Integer.parseInt(list.get("GATESID"));
        p.R01ID = Integer.parseInt(list.get("R01ID"));
        p.numBands = Integer.parseInt(list.get("numBands"));

        p.getSlaughterAge("pig");

    }

    /*
    //====================================================
    public void readEggFromFile(HashMap<String , String> list, Human human)
    {

    //for(String key : list.keySet())
    //{
    //    System.out.println(key + ": " + list.get(key));
    //}

    Egg e = new Egg((SimState)simVilla, human);

    e.identity = Integer.parseInt(list.get("identity"));

    e.age = Integer.parseInt(list.get("age"));

    if(list.get("proglottid").equals("true"))e.proglottid = true;
    else e.proglottid = false;

    if(list.get("ova").equals("true"))e.ova = true;
    else e.ova = false;

    if(list.get("travelingHuman").equals("true"))e.travelingHuman = true;
    else e.travelingHuman = false;

    e.contRadiusEgg = Double.parseDouble(list.get("contRadiusEgg"));

    e.timeSinceInfection = Integer.parseInt(list.get("timeSinceInfection"));

    e.infectionDuration = Integer.parseInt(list.get("infectionDuration"));

    }
    */

    //====================================================
    public void writeVillageToFile(String what, Boolean append)
    {
        if(simVilla.extendedOutput)if(simVilla.cystiHumans)System.out.println("cystsHumanBag size: " + simVilla.humanCystsBag.size());

        /*
        int statsC = 0;
        for(int i = 0; i < simVilla.humansBag.size(); i++)
        {
            Human h = (Human)simVilla.humansBag.get(i);
            statsC = statsC + h.cysts.size();
        }
        System.out.println("cysts human bag sizE from humans: " + statsC);

        statsC = 0;
        for(int i = 0; i < simVilla.humanCystsBag.size(); i++)
        {
            HumanCyst hc = (HumanCyst)simVilla.humanCystsBag.get(i);
            if(hc.human == null)statsC = statsC + 1;
            System.out.println("human cyst id: " + hc.human.identity);
            if(hc.human.dead)System.out.println("human of a huamnCyst dead");
            if(hc.human.traveler)System.out.println("human of a humanCyst traveler");
            if(hc.human.strangerTraveler)System.out.println("human of a humanCyst strangerTraveler");
        }
        System.out.println("cysts with null human: " + statsC);

        statsC = 0;
        for(int i = 0; i < simVilla.humanCystsBag.size(); i++)
        {
            HumanCyst hc = (HumanCyst)simVilla.humanCystsBag.get(i);
            if(hc.dead)statsC = statsC + 1;
        }
        System.out.println("cysts dead: " + statsC);
        */
        //------------------

        List<String> lines = new ArrayList<String>();

        lines.add("\n");

        String line = "#######################" + "\n";
        lines.add(line);
        //line = "#----------------------" + "\n";
        lines.add(line);

        String outputFile = "";

        line ="VillageAgentStart " + name + "\n";

        if(what.equals("onlyHumans"))
        {
            outputFile = simVilla.outDir + "/demoModule_PictureOfTheVillage.txt";
        }
        else if(what.equals("all"))
        {
            outputFile = simVilla.deterministicIndividualsAllocationFile;
        }

        lines.add(line);

        writeHouseholdsToString(lines, what);
        //writePigsToString(line);

        line = "#----------------------" + "\n";
        lines.add(line);

        line ="VillageAgentEnd: " + name + "\n";
        lines.add(line);
        //System.out.println (lines);

        line = "#######################" + "\n";
        lines.add(line);


        if((simVilla.villageDataset.equals("R01")
                && simVilla.readPopFromSurveyCensusFile 
                && !simVilla.deterministicIndividualsAllocation.equals("read"))
                || simVilla.simW.allVillagesInOneSim)
        {
            writeObsDataR01(lines);
        }

        //System.exit(0);


        File f = null;
        FileWriter w = null;
        //System.out.println(simVilla.deterministicIndividualsAllocationFile);

        try{
            f = new File(outputFile);
            //f = new File("./outputs/pippo.txt");
            w = new FileWriter(f, append);

            for(int i = 0; i < lines.size(); i++)
            {
                line = (String)lines.get(i);
                w.write(line);
            }

            w.close();

        } catch (IOException ex) {
            System.out.println(ex);

        }

        if(simVilla.extendedOutput)System.out.println("Village written to the file");
        //System.exit(0);

    }


    //====================================================
    public void writeHouseholdsToString(List<String> lines, String what)
    {
        for(int i = 0; i < simVilla.householdsBag.size(); i++)
        {
            Household hh = (Household)simVilla.householdsBag.get(i);

            String line = "#----------------------" + "\n";
            lines.add(line);

            line = "HouseholdAgentStart " + "\n";
            lines.add(line);

            line ="simId " + hh.simId + "\n";
            lines.add(line);

            line ="trialId " + hh.trialId + "\n";
            lines.add(line);

            line ="shpId " + hh.shpId + "\n";
            lines.add(line);

            line ="type " + hh.type + "\n";
            lines.add(line);

            if(hh.pigOwner)line = "pigOwner " + "true" + "\n";
            else line = "pigOwner " + "false" + "\n";
            lines.add(line);

            line ="corralUse " + hh.corralUse + "\n";
            lines.add(line);

            if(hh.corral)line = "corral " + "true" + "\n";
            else line = "corral " + "false" + "\n";
            lines.add(line);

            if(hh.latrine)line = "latrine " + "true" + "\n";
            else line = "latrine " + "false" + "\n";
            lines.add(line);

            if(hh.latrineUsers)line = "latrineUsers " + "true" + "\n";
            else line = "latrineUsers " + "false" + "\n";
            lines.add(line);

            if(hh.sewerUsers)line = "sewerUsers " + "true" + "\n";
            else line = "sewerUsers " + "false" + "\n";
            lines.add(line);

            if(hh.travelerHh)line = "travelerHh " + "true" + "\n";
            else line = "travelerHh " + "false" + "\n";
            lines.add(line);

            line ="contRadiusHh " + hh.contRadiusHh + "\n";
            lines.add(line);

           line ="waterSuplyType " + hh.waterSuplyType + "\n";
            lines.add(line);

            line ="numPigsCorraled " + hh.numPigsCorraled + "\n";
            lines.add(line);

            line ="numPigsAmarrados " + hh.numPigsAmarrados + "\n";
            lines.add(line);

            line ="numPigsFree " + hh.numPigsFree + "\n";
            lines.add(line);

            line ="distClose " + hh.distClose + "\n";
            lines.add(line);

            line ="targetNumOfPigs " + hh.targetNumOfPigs + "\n";
            lines.add(line);

            //CystiHumans
            if(simVilla.cystiHumans)
            {
                line ="numberOfTapewormCarriers " + hh.numberOfTapewormCarriers + "\n";
                lines.add(line);
            }

            line = "HouseholdAgentDataEnd " + "\n";
            lines.add(line);

            for(int j = 0; j < hh.humans.size(); j++)
            {
                Human h = (Human)hh.humans.get(j);
                writeHumanToString(lines, h);
            }

            if(what.equals("all"))
            {
                for(int j = 0; j < hh.pigs.size(); j++)
                {
                    Pig p = (Pig)hh.pigs.get(j);
                    writePigToString(lines, p);
                }
            }


            line = "HouseholdAgentEnd " + "\n";
            lines.add(line);

        }

    }

    //====================================================
    public void writePigToString(List<String> lines, Pig h)
    {
        String line = "#----------------------" + "\n";
        lines.add(line);

        line ="PigAgentStart " + "\n";
        lines.add(line);

        line ="identity " + h.identity + "\n";
        lines.add(line);

        line ="censusIdentity " + h.censusIdentity + "\n";
        lines.add(line);

        line ="age " + h.age + "\n";
        lines.add(line);

        line ="numCysts " + h.numCysts + "\n";
        lines.add(line);

        if(h.seropositive)line = "seropositive " + "true" + "\n";
        else line = "seropositive " + "false" + "\n";
        lines.add(line);

        if(h.heavyInfected)line = "heavyInfected " + "true" + "\n";
        else line = "heavyInfected " + "false" + "\n";
        lines.add(line);

        if(h.markedForSlaughter)line = "markedForSlaughter " + "true" + "\n";
        else line = "markedForSlaughter " + "false" + "\n";
        lines.add(line);

        if(h.eligibleP)line = "eligibleP " + "true" + "\n";
        else line = "eligibleP " + "false" + "\n";
        lines.add(line);

        if(h.treated)line = "treated " + "true" + "\n";
        else line = "treated " + "false" + "\n";
        lines.add(line);

        line ="treatmentProtectedTime " + h.treatmentProtectedTime + "\n";
        lines.add(line);

        line ="stopSlaughterTimer " + h.stopSlaughterTimer + "\n";
        lines.add(line);

        line ="vaccDose " + h.vaccDose + "\n";
        lines.add(line);

        if(h.vaccinated)line = "vaccinated " + "true" + "\n";
        else line = "vaccinated " + "false" + "\n";
        lines.add(line);

        line ="corraled " + h.corraled + "\n";
        lines.add(line);

        line ="slaughterAge " + h.slaughterAge + "\n";
        lines.add(line);

        line ="homeRange " + h.homeRange + "\n";
        lines.add(line);

        line ="homeRangeArea " + h.homeRangeArea + "\n";
        lines.add(line);

        if(h.dead)line = "dead " + "true" + "\n";
        else line = "dead " + "false" + "\n";
        lines.add(line);

        if(h.treated)line = "isCorraled " + "true" + "\n";
        else line = "isCorraled " + "false" + "\n";
        lines.add(line);

        if(h.imported)line = "imported " + "true" + "\n";
        else line = "imported " + "false" + "\n";
        lines.add(line);

        line ="numSteps " + h.numSteps + "\n";
        lines.add(line);

        line ="numDefecationSitesInHomeRange " + h.numDefecationSitesInHomeRange + "\n";
        lines.add(line);

        line ="numDegeneratedCystsIc " + h.numDegeneratedCystsIc + "\n";
        lines.add(line);
        line ="numDegeneratedCystsIcThisTimeStep " + h.numDegeneratedCystsIcThisTimeStep + "\n";
        lines.add(line);
        line ="numDegeneratedCystsIi " + h.numDegeneratedCystsIi + "\n";
        lines.add(line);
        line ="numCystsFromProglottids " + h.numCystsFromProglottids + "\n";
        lines.add(line);
        line ="numCystsFromEggs " + h.numCystsFromEggs + "\n";
        lines.add(line);

        line ="immunityO " + h.immunityO + "\n";
        lines.add(line);
        line ="immunityC " + h.immunityC + "\n";
        lines.add(line);


        //if(h.pregnant)line = "pregnant " + "true" + "\n";
        //else line = "pregnant " + "false" + "\n";
        //lines.add(line);
        //if(h.breedingSow)line = "breedingSow " + "true" + "\n";
        //else line = "breedingSow " + "false" + "\n";
        //lines.add(line);

        line ="GATESID " + h.GATESID + "\n";
        lines.add(line);
        line ="R01ID " + h.R01ID + "\n";
        lines.add(line);
        line ="numBands " + h.numBands + "\n";
        lines.add(line);

        line ="PigAgentEnd " + "\n";
        lines.add(line);
    }

    //====================================================
    public void writeHumanToString(List<String> lines, Human h)
    {
        String line = "#----------------------" + "\n";
        lines.add(line);
        line ="HumanAgentStart " + "\n";
        lines.add(line);
        line ="identity " + h.identity + "\n";
        lines.add(line);
        line ="age " + h.age + "\n";
        lines.add(line);

        line ="gender " + h.gender + "\n";
        lines.add(line);

        line ="education " + h.education + "\n";
        lines.add(line);

        line ="famRelation " + h.famRelation + "\n";
        lines.add(line);

        if(h.studyParticipation)line = "studyParticipation " + "true" + "\n";
        else line = "studyParticipation " + "false" + "\n";
        lines.add(line);

        if(h.acceptedNMTreatment)line = "acceptedNMTreatment " + "true" + "\n";
        else line = "acceptedNMTreatment " + "false" + "\n";
        lines.add(line);

        line ="GATESID " + h.GATESID + "\n";
        lines.add(line);


        if(h.latrineUser)line = "latrineUser " + "true" + "\n";
        else line = "latrineUser " + "false" + "\n";
        lines.add(line);

        if(h.sewerUser)line = "sewerUser " + "true" + "\n";
        else line = "sewerUser " + "false" + "\n";
        lines.add(line);

        if(h.tapeworm)line = "tapeworm " + "true" + "\n";
        else line = "tapeworm " + "false" + "\n";
        lines.add(line);

        if(h.traveling)line = "traveling " + "true" + "\n";
        else line = "traveling " + "false" + "\n";
        lines.add(line);

        if(h.traveler)line = "traveler " + "true" + "\n";
        else line = "traveler " + "false" + "\n";
        lines.add(line);

        line ="travelDuration " + h.travelDuration + "\n";
        lines.add(line);

        line ="timeToTheNextTravel " + h.timeToTheNextTravel + "\n";
        lines.add(line);

        line ="timeSinceInfection " + h.timeSinceInfection + "\n";
        lines.add(line);

        line ="infectionDuration " + h.infectionDuration + "\n";
        lines.add(line);

        if(h.tapeworm)line = "tapeworm " + "true" + "\n";
        else line = "tapeworm " + "false" + "\n";
        lines.add(line);

        if(h.tapewormMature)line = "tapewormMature " + "true" + "\n";
        else line = "tapewormMature " + "false" + "\n";
        lines.add(line);

        if(h.eligibleH)line = "eligibleH " + "true" + "\n";
        else line = "eligibleH " + "false" + "\n";
        lines.add(line);

        line ="numWeekSteps " + h.numWeekSteps + "\n";
        lines.add(line);

        line ="famRelation " + h.famRelation + "\n";
        lines.add(line);

        if(h.emigrated)line = "emigrated " + "true" + "\n";
        else line = "emigrated " + "false" + "\n";
        lines.add(line);

        //for(int j = 0; j < h.eggs.size(); j++)
        //{
        //    Egg e = (Egg)h.eggs.get(j);

        //    writeEggsToString(lines, e);
        //}

        //CystiHumans
        if(simVilla.cystiHumans)
        {
            //the cook will be selected after the village picture reading
            //if(h.cook)line = "cook " + "true" + "\n";
            //else line = "cook " + "false" + "\n";
            //lines.add(line);

            line ="epiStatus " + h.epiStatus + "\n";
            lines.add(line);

            if(h.ichHum)line = "ichHum " + "true" + "\n";
            else line = "ichHum " + "false" + "\n";
            lines.add(line);

            line ="epiTreat " + h.epiTreat + "\n";
            lines.add(line);

            if(h.epiTreatSuccess)line = "epiTreatSuccess " + "true" + "\n";
            else line = "epiTreatSuccess " + "false" + "\n";
            lines.add(line);

            line ="ichTreatment " + h.ichTreatment + "\n";
            lines.add(line);

            line ="ichTreatDelay " + h.ichTreatDelay + "\n";
            lines.add(line);
        }

        line ="HumanAgentDataEnd " + "\n";
        lines.add(line);

        if(simVilla.cystiHumans)
        {
            //System.out.println ("printing humans cysts");
            for(int j = 0; j < h.cysts.size(); j++)
            {
                //System.out.println ("Human cyst");
                //System.exit(0);
                HumanCyst cyst = (HumanCyst)h.cysts.get(j);
                writeHumanCystToString(lines, cyst);
            }
        }


        line ="HumanAgentEnd " + "\n";
        lines.add(line);

    }

    //====================================================
    public void writeHumanCystToString(List<String> lines, HumanCyst cyst)
    {
        String line = "#----------------------" + "\n";
        lines.add(line);
        line ="HumanCystAgentStart " + "\n";
        lines.add(line);
        line ="age " + cyst.age + "\n";
        lines.add(line);

        line ="identity " + cyst.identity + "\n";
        lines.add(line);

        if(cyst.parLoc)line = "parLoc " + "true" + "\n";
        else line = "parLoc " + "false" + "\n";
        lines.add(line);

        line ="stage " + cyst.stage + "\n";
        lines.add(line);

        line ="tau2 " + cyst.tau2 + "\n";
        lines.add(line);

        line ="tau3 " + cyst.tau3 + "\n";
        lines.add(line);

        line ="ts " + cyst.ts + "\n";
        lines.add(line);

        line ="t1s " + cyst.t1s + "\n"; // GB12mars
        lines.add(line); // GB12mars

        if(cyst.ichCyst)line = "ichCyst " + "true" + "\n";
        else line = "ichCyst " + "false" + "\n";
        lines.add(line);


        //------------------------
        line ="HumanCystAgentEnd " + "\n";
        lines.add(line);

        //System.out.println (lines);
        //System.out.println ("Human cyst");

        //System.exit(0);

    }



    /*
    //====================================================
    public void writeEggsToString(List<String> lines, Egg e)
    {
    String line = "#----------------------" + "\n";
    lines.add(line);

    line ="EggAgentStart " + "\n";
    lines.add(line);

    line ="identity " + e.identity + "\n";
    lines.add(line);

    line ="age " + e.age + "\n";
    lines.add(line);

    if(e.proglottid)line = "proglottid " + "true" + "\n";
    else line = "proglottid " + "false" + "\n";
    lines.add(line);

    if(e.ova)line = "ova " + "true" + "\n";
    else line = "ova " + "false" + "\n";
    lines.add(line);

    if(e.travelingHuman)line = "travelingHuman " + "true" + "\n";
    else line = "travelingHuman " + "false" + "\n";
    lines.add(line);

    line ="contRadiusEgg " + e.contRadiusEgg + "\n";
    lines.add(line);

    line ="timeSinceInfection " + e.timeSinceInfection + "\n";
    lines.add(line);

    line ="infectionDuration " + e.infectionDuration + "\n";
    lines.add(line);

    line ="EggAgentEnd " + "\n";
    lines.add(line);


    }
    */


    //====================================================
    public void resetInfections()
    {
        SimState state = (SimState)simVilla;

        //reset human infection state
        for(int i = 0; i < simVilla.humansBag.size(); i++)
        {
            Human human = (Human)simVilla.humansBag.get(i);

            human.matureTn(false);

            if(state.random.nextDouble() < simVilla.baselineTnPrev)
            {
                //System.out.println ("InfectHuman baseline " + simVilla.humansIds);
                human.infectHumanBaseline();
            }
        }

        //reset pigs infection state
        for(int i = 0; i < simVilla.pigsBag.size(); i++)
        {
            Pig pig = (Pig)simVilla.pigsBag.get(i);

            pig.numCysts = 0;

            //System.out.println ("Pig: " + pig.identity);

            double rand = state.random.nextDouble();
            if(rand < simVilla.importPrev)
            {
                simVilla.pigsGen.assignPigCysts(pig);
                //System.out.println ("ncysts assigned: " + pig.numCysts);
            }
        }


    }

    //====================================================
    public void writeObsDataR01(List<String> lines)
    {
        if(simVilla.extendedOutput)System.out.println ("Writing R01 observed pig serology data");

        String line;

        line = "#######################" + "\n";
        lines.add(line);

        line ="R01serologyDataStart\n";
        lines.add(line);

        for(int i = 0; i < 8; i++)
        {
            line ="obsSeroIncidencePigsRounds" + i + " " + simVilla.obsSeroIncidencePigsRounds.get(i) + "\n";
            lines.add(line);

            line ="obsSeroPrevalencePigsRounds" + i + " " + simVilla.obsSeroPrevalencePigsRounds.get(i) + "\n";
            lines.add(line);

            line ="obsSeroPrevalencePigletsRounds" + i + " " + simVilla.obsSeroPrevalencePigletsRounds.get(i) + "\n";
            lines.add(line);

            line ="obsPrevTonguePositiveRounds" + i + " " + simVilla.obsPrevTonguePositiveRounds.get(i) + "\n";
            lines.add(line);
        }

        line ="R01serologyDataEnd\n";
        lines.add(line);

        //if(simVilla.extendedOutput)System.out.println (lines);

        //System.exit(0);

    }

    //====================================================
    public void R01ReadObsDataFromFile(HashMap<String , String> list)
    {
        Boolean printOut = false;

        for(int i = 0; i < 8; i++)
        {
            simVilla.obsSeroIncidencePigsRounds.add(0.0);
            simVilla.obsSeroPrevalencePigsRounds.add(0.0);
            simVilla.obsOverallSeroPrevalencePigsRounds.add(0.0);
            simVilla.obsSeroPrevalencePigletsRounds.add(0.0);

            simVilla.obsPrevTonguePositiveRounds.add(0.0);
        }

        String tmp = "";

        for(int i = 0; i < 8; i++)
        {
            tmp = "obsSeroIncidencePigsRounds" + i;


            simVilla.obsSeroIncidencePigsRounds.set(i, Double.parseDouble(list.get(tmp)));
            if(simVilla.extendedOutput)if(printOut)System.out.println ("obseSeroIncidencePigsRounds, round " + i  + ": " + simVilla.obsSeroIncidencePigsRounds.get(i));

            tmp = "obsSeroPrevalencePigsRounds" + i;
            simVilla.obsSeroPrevalencePigsRounds.set(i, Double.parseDouble(list.get(tmp)));
            if(simVilla.extendedOutput)if(printOut)System.out.println ("obseSeroPrevalencePigsRounds, round " + i  + ": " + simVilla.obsSeroPrevalencePigsRounds.get(i));

            tmp = "obsSeroPrevalencePigletsRounds" + i;

            //if(simVilla.extendedOutput)System.out.println (list);
            //if(simVilla.extendedOutput)System.out.println (tmp);
            //if(simVilla.extendedOutput)System.out.println (list.get(tmp));
            //if(simVilla.extendedOutput)System.out.println (Double.parseDouble(list.get(tmp)));

            simVilla.obsSeroPrevalencePigletsRounds.set(i, Double.parseDouble(list.get(tmp)));
            if(simVilla.extendedOutput)if(printOut)System.out.println ("obseSeroPrevalencePigletsRounds, round " + i  + ": " + simVilla.obsSeroPrevalencePigletsRounds.get(i));

            tmp = "obsPrevTonguePositiveRounds" + i;
            simVilla.obsPrevTonguePositiveRounds.set(i, Double.parseDouble(list.get(tmp)));
            if(simVilla.extendedOutput)if(printOut)System.out.println ("obsePrevTonguePositiveRounds, round " + i  + ": " + simVilla.obsPrevTonguePositiveRounds.get(i));
        }
    }

    //====================================================
    public void rmDirs()
    {

        File theDir = new File(simVilla.outDirSims);
        if(simVilla.extendedOutput)System.out.println(simVilla.villageName + " removing directory: " + simVilla.outDirSims);
        int nMax = 0;

        // if the directory does not exist, create it
        if (theDir.exists())
        {
            try{      
                Boolean bool = theDir.delete();
                if(!bool)if(simVilla.extendedOutput)System.out.println(simVilla.villageName + " directory: " + simVilla.outDirSims + " not removed");

            }catch(Exception e){
                // if any error occurs
                e.printStackTrace();
            }

            if(simVilla.extendedOutput)System.out.println(simVilla.villageName + " removed directory: " + simVilla.outDirSims);
        }


        theDir = new File(simVilla.outDir);
        if(simVilla.extendedOutput)System.out.println(simVilla.villageName + " removing directory: " + simVilla.outDir);
        nMax = 0;

        // if the directory does not exist, create it
        if (theDir.exists())
        {
            try{      
                Boolean bool = theDir.delete();
                if(!bool)if(simVilla.extendedOutput)System.out.println(simVilla.villageName + " directory: " + simVilla.outDir + " not removed");

            }catch(Exception e){
                // if any error occurs
                e.printStackTrace();
            }

        }






    }
    


}//End of file

