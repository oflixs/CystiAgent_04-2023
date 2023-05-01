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

//===============================================
public class VillageStats
{ 

    public CystiAgents sim = null;
    public SimState state = null;

    public String name;
    public int nameNumb;

    public int nHumans = 0;
    public int nHouses = 0;
    public int nPigs = 0;

    public String interventionType = "";
    public int R01InterventionArm = 0;

    public List<String> housesNumbers = new ArrayList<String>();

    //Village paramenters 

    //===============================================
    public VillageStats(final SimState pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;

    }

    //===============================================
    public void printResume()
    {
        if(sim.extendedOutput)System.out.println("--- Village -----------------");
        if(sim.extendedOutput)System.out.println("Name: " + name);
        if(sim.extendedOutput)System.out.println("R01 Intervention Arm: " + R01InterventionArm);
        if(sim.extendedOutput)System.out.println("R01 Intervention tyoe: " + sim.villageIntArm.get(name));
        if(sim.extendedOutput)System.out.println("Num houses: " + nHouses);
        if(sim.extendedOutput)System.out.println("Num humans: " + nHumans);
        if(sim.extendedOutput)System.out.println("Num pigs: " + nPigs);

    }

    //===============================================
    public void addHuman()
    {
        nHumans++;
    }

    //===============================================
    public void addPig()
    {
        nPigs++;
    }




}//End of file

