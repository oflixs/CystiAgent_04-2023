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

import com.vividsolutions.jts.geom.Point;

//----------------------------------------------------
public class CensedHousehold 
{
    private static final long serialVersionUID = 1L;

    //public Household household = null;

    //Parameters from CystiAgents NetLogo model
    public Pig pig = null;

    public double numHumans = 0.0;
    public double numHumansInRange = 0.0;
    public double numPigs = 0.0;

    public double numInfectedHumans = 0.0;
    public double numInfectedHumansInRange = 0.0;
    public double numInfectedPigs = 0.0;
    public double numInfectedPigsInRange = 0.0;
    public double genderRatio = 0.0;
    public double genderRatioInRange = 0.0;
    public double genderRatioInfected = 0.0;
    public double genderRatioInfectedInRange = 0.0;
    public double numPigsInRange = 0.0;

    public double nElim1InRange = 0;
    public double nSshh4InRange = 0;
    public double corrCondInRange = 0;
    public double corrMatInRange = 0;
    public double outDefecatorsInRange = 0;
    public double noBathroomInRange = 0;
    public double scaledEliminahecValueInRange = -100;
    public double scaledSshhValueInRange = -100;
    public double scaledCorrCondInRange = -100;
    public double scaledCorrMatInRange = -100;



    public int seropositivePigs  = 0;
    public int pigsInRange  = 0;
    public int pigsInRangePositive  = 0;

    public int sshhValue = 0;
    public double sshhValueInRange = 0;
    public int eliminahecValue = 0;
    public double eliminahecValueInRange = 0;

    public double avgDistFromVillage = 0.0;

    public double distFromVillageCenter = 0.0;

    public int centerDistDecile = 0;

    public double avgPigsSeroprevalence = 0.0;

    public double hhInRange = 0;

    public double pigsSeroprevalenceInRange = 0.0;

    public Point point = null;

    int ID = 0;
    int shpId = 0;
    String village = "";
    String name = "";

    public List<Integer> pigsInRangeRound = new ArrayList<Integer>(); 
    public List<Integer> pigsInRangePositiveRound = new ArrayList<Integer>(); 

    //====================================================
    public CensedHousehold(String pname)
    {
        name = pname;
    }

    //====================================================
    public void printResume()
    {
        System.out.println(" ");
        System.out.println("---- Censed Household Resume ------------------");
        System.out.println("Name: " + name);
        System.out.println("Num humans: " + numHumans + " infected: " + numInfectedHumans);
        System.out.println("Num pigs: " + numPigs + " infected: " + numInfectedPigs);
        System.out.println("sshhValue: " + sshhValue);
        System.out.println("eliminahecValue: " + eliminahecValue);
        System.out.println("avg. distance from village hh: " + avgDistFromVillage);
        System.out.println("number of hh in range: " + hhInRange);
        System.out.println("Avg. pigs seroprevalence: " + avgPigsSeroprevalence);
        System.out.println("Avg. pigs seroprevalence in range: " + pigsSeroprevalenceInRange);
        System.out.println("distance from village center decile :" + centerDistDecile);

        //System.out.println("Coords: " + geoPoint.getX() + " " + geoPoint.getY());
        //System.exit(0);

    }

 


}


