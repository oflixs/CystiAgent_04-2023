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
public class DefecationSite implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public Stoppable stopper;

    public int identity = 0;

    public Human human = null;//human that produced the contamination

    public Boolean proglottid = false;//if true heavy contamiantion
    public Boolean eggs = false;//if true light contamination
    public Boolean eggsAlive  = false;//if true eggs are still viable afote the tapeworm died

    //public CoverPixel cpPosition = null;//where it was deposited
    //public void setCpPosition(CoverPixel mg){cpPosition = mg;};
    //public CoverPixel getCpPosition(){return cpPosition;};

    public Boolean setToDie  = false;//true if the human left the village or is dead but there are still eggs in the defecation site.
    public int setToDieTimer = -100;

    public Point pointPos = null;

    public Boolean joinedFromOtherVillage = false;

    //boolean tagged = false;

    //====================================================
    public DefecationSite(SimState pstate, Human ph)
    {
        state = pstate;
        sim = (CystiAgents)state;

        identity = sim.contaminationSitesIds;
        sim.contaminationSitesIds++;

        //System.out.println("---- New Egg");
        human = ph;

        sim.defecationSitesBag.add(this);

        double interval = 1.0;
        this.stopper = sim.schedule.scheduleRepeating(this, 12, interval);

        //cpPosition = cp;

        //sim.defecationSiteGrid.setObjectLocation(this, 
        //        sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[0],
        //        sim.utils.getCoordsToDisplay(cpPosition.getXcor(), cpPosition.getYcor(), "geo")[1]
        //        );

        //System.out.println("Num eggs: " + sim.eggsBag.size());

    }
    

    //====================================================
    public void step(SimState state)
    {
        //System.out.println("---- Defecation site step");

        //int now = (int)state.schedule.getTime();  

        if(eggsAlive && !proglottid)environmentalDecay();

        if(setToDie)
        {
            setToDieTimer--;
            if(setToDieTimer <= 0)die();
        }
    }

    //====================================================
    public void environmentalDecay()
    {
        double rand = state.random.nextDouble();
        //if(!eggs && proglottid)System.out.println("eggs test");
        if(rand < sim.decayMean)
        {
            eggs = false;
            eggsAlive = false;

            if(human == null && !setToDie)switchToSetToDie();

            //if(human.identity == 140)
            //{
            //    System.out.println("eggs environmentallllll decay.........");
            //}
        }

    }

    //===============================================
    public  void die()
    {
        sim.defecationSitesBag.remove(this);
        eggs = false;
        eggsAlive = false;
        proglottid = false;

        this.stopper.stop();
        return;
    }


    //===============================================
    public  void switchToSetToDie()
    {
        double rand = 0.0;
        int stats = 1;
        setToDie = true;

        Boolean toDie = true;
        while(toDie)
        {

            rand = state.random.nextDouble();
            //System.out.println("eggs test");
            if(rand < sim.decayMean)
            {
                toDie = false;
                setToDieTimer = stats;
            }
            stats++;
        }
    }
}


