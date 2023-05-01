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

public class CystiAgentsWorld   extends SimState{

    private static final long serialVersionUID = 1;

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

    //to study villages stats
    public List<CensedHousehold> censedHouses = new ArrayList<CensedHousehold>();
    public int numDeciles = 5;
    public double rangeLimit = 500.0;

    List<Household> households = new ArrayList<Household>(); 
    List<CensedPig> censedPigs = new ArrayList<CensedPig>(); 
    public Boolean studyVillages = false;

    //to simulate all the villages listed in the input file in one unique silulation area
    public boolean allVillagesInOneSim = false;

    //====================================================
    public CystiAgentsWorld(long seed)
    {
        super(seed);
    }

    //====================================================
    public void readInput()
    {
        worldInputFile = "paramsFiles/" + worldInputFile; 

        //if(ABC)System.out.println (worldInputFile);
        input = new ReadInput(worldInputFile, rootDir, false);

        villagesNames = input.readListString("villagesNames");
        numVilla = villagesNames.size();

        String tmp = "";

        tmp = input.readString("allVillagesInOneSim");
        //System.out.println (villageName + ": allVillagesInOneSim = " + tmp);
        if(tmp.equals("true"))allVillagesInOneSim = true;
        else allVillagesInOneSim = false;

        getVillagesNamesNumbers();

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
    public void start()
    { 
        //--------------------------------------------------------
        //Start thread with the module to generate long range human movements
        //Internal villages names: Villa0, Villa1, ...
        if(!ABC)System.out.println (" ");
        if(!ABC)System.out.println ("==== Starting Taenia World Simulation ============");

        super.start();

        //Read input in world input file -----------------------
        readInput();

        //Print starting simulation test ----------------
        if(!ABC)System.out.println ("Simulation name: " + simName);

        if(!ABC)System.out.println (" ");
        if(!ABC)System.out.println ("==================================================");
        if(!ABC)System.out.println ("==== Simulation CystiAgents World Launched =========");
        if(!ABC)System.out.println (" ");

        if(ABC)System.out.println ("World input file name: " + worldInputFile);

        if(allVillagesInOneSim)
        {
            startAllVillagesInOneSim();
            return;
        }

        //System.exit(0);

        //--------------------------------------
        //--------------------------------------
        //studyVillages = true;
        //StudyVillagesStats villStats = new StudyVillagesStats(this);
        //villStats.studyVillagesDensity();
        //System.exit(0);
        //--------------------------------------
        //--------------------------------------

        //Starts the villages simulations --------
        CyclicBarrier barrier = null;

        barrier = new CyclicBarrier(numVilla);

        for (int j = 0; j < numVilla; j++)
        {
            String name = villagesNames.get(j);

            if(!ABC)System.out.println ("Village name from world: ----" + name + "------");

            Village village = new Village(System.currentTimeMillis(), j, this, barrier);

            villages.add(village);

            schedule.scheduleRepeating(village);

            village.init();

            village.start();

            numRunning++;

            try {         
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println ("Before contrl");
        ctrlThread = new ControlThread(this, barrier);
        schedule.scheduleRepeating(ctrlThread);
        ctrlThread.start();

        //System.exit(0);


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
            System.out.println ("Village name: " + name + " number name: " +  words[1] + " group: " + words[0]);
        }
        //System.exit(0);
    }



    //===============================================
    public static void main(String[] args){
        //System.out.println (System.getProperty("java.classpath"));

        if(args.length == 2)
        {
            ABC = true;
            ABCTime = args[1];
        }

        simName = args[0];

        worldInputFile = simName + "_coreInput.params";

        //System.exit(0);

        CystiAgentsWorld simW = new CystiAgentsWorld(System.currentTimeMillis());
        //CystiAgentsWorld simW = new CystiAgentsWorld(100);

        simW.start();

    }



    //====================================================
    public void startAllVillagesInOneSim()
    { 
        System.out.println ("---- World Mode: all villages in one sim ----");

        //System.exit(0);

        //Starts the villages simulations --------
        CyclicBarrier barrier = null;
        barrier = new CyclicBarrier(numVilla + 1);

        ctrlThread = new ControlThread(this, barrier);
        ctrlThread.start();

        String joinedVillageName = "";

        for (int j = 0; j < numVilla; j++)
        {
            String name = villagesNames.get(j);
            if(j == 0)joinedVillageName = name;
            else joinedVillageName = joinedVillageName + "_" + name;
        }

        for (int j = 0; j < numVilla; j++)
        {
            String name = villagesNames.get(j);

            if(!ABC)System.out.println ("Village name from world: ----" + name + "------");

            Village village = new Village(System.currentTimeMillis(), j, this, barrier);

            villages.add(village);

            village.init();

            village.start();

            if(j == 0)
            {
                village.setMainJointVillage(true);
                village.setJoinedName(joinedVillageName);
            }
            else if (j > 0)
            {
                village.stopSim();
            }

            numRunning++;

            try {         
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println ("aaa num threads in the barrier: " + barrier.getParties());
        System.out.println ("aaa 1  num threads waiting: " + barrier.getNumberWaiting());
        //System.exit(0);

        Boolean allDone = false;
        while(!allDone)
        {
            Boolean tmp = true;
            for (int j = 0; j < villages.size(); j++)
            {
                Village village = villages.get(j);
                CystiAgents simVilla = village.getsimVilla();
                if(!simVilla.startAllDone)tmp = false;
            }
            if(tmp)allDone = true;
        }

        //System.exit(0);

        System.out.println ("2 num threads waiting: " + barrier.getNumberWaiting());

        System.out.println ("all vilages 2");

        JoinVillages jVill = new JoinVillages(this); 
        jVill.joinVillages();

        System.out.println ("3 num threads waiting: " + barrier.getNumberWaiting());

        System.out.println ("all vilages 3");

        //System.exit(0);
        ctrlThread.controlaWait();
        System.out.println ("after control thread await num threads waiting: " + barrier.getNumberWaiting());

        //System.exit(0);

    }



}//End of file

