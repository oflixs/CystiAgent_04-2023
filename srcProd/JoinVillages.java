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
import java.util.*; 

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import com.vividsolutions.jts.geom.LinearRing;
import sim.util.geo.MasonGeometry;


//----------------------------------------------------
public class JoinVillages 
{
    private static final long serialVersionUID = 1L;

    public final CystiAgentsWorld simW;

    public int maxShpId = -100;
    public int maxHouseholdId = -100;
    public int maxHumanId = -100;
    public int maxPigId = -100;
    public int maxPigCensusId = -100;
    public int maxDefSId = -100;
    public int maxCystsId = -100;

    public Village villageBase;
    public CystiAgents simBase;

    //====================================================
    public JoinVillages(CystiAgentsWorld psimW)
    {
        simW = psimW;
    }

    //====================================================
    public void joinVillages()
    {
        System.out.println ("---- Joining all the willages together ----");
        villageBase = simW.villages.get(0);
        simBase = villageBase.getsimVilla();

        System.out.println (" ");
        System.out.println ("--------------------------------------------------------");
        System.out.println ("Stats of the base village:");
        simBase.householdsGen.printHouseholdsStats();

        getMaxIds();

        System.out.println ("maxSphId: " + maxShpId);
        System.out.println ("maxHouseholdId: " + maxShpId);
        System.out.println ("maxHumanId: " + maxHumanId);
        System.out.println ("maxPigId: " + maxPigId);
        System.out.println ("maxPigCensusId: " + maxPigCensusId);

        for (int j = 1; j < simW.villages.size(); j++)
        {
            Village village = simW.villages.get(j);
            CystiAgents simVilla = village.getsimVilla();

            //System.out.println ("maxSphId: " + maxShpId);

            System.out.println ("new village num hh: " + simVilla.householdsBag.size());

            for(int i = 0; i < simVilla.householdsBag.size(); i++)
            {
                Household hh = (Household)simVilla.householdsBag.get(i);

                hh.joinedFromOtherVillage = true;

                hh.sim = simBase;
                hh.state = (SimState)simBase;

                simBase.householdsBag.add(hh);

                hh.stopper.stop();

                double interval = 1.0;
                hh.stopper = simBase.schedule.scheduleRepeating(hh, 6, interval);

                maxShpId++;
                hh.shpId = maxShpId;

                maxHouseholdId++;
                hh.simId = maxHouseholdId;

                hh.villageNameNumber = simBase.villageNameNumber;

            }

            simVilla.householdsBag = new Bag();

            for(int i = 0; i < simVilla.humansBag.size(); i++)
            {
                Human h = (Human)simVilla.humansBag.get(i);

                h.joinedFromOtherVillage = true;

                h.sim = simBase;
                h.state = (SimState)simBase;

                simBase.humansBag.add(h);

                h.stopper.stop();

                double interval = 1.0;
                h.stopper = simBase.schedule.scheduleRepeating(h, 10, interval);

                maxHumanId++;
                h.identity = maxHumanId;

            }

            simVilla.humansBag = new Bag();

            for(int i = 0; i < simVilla.pigsBag.size(); i++)
            {
                Pig p = (Pig)simVilla.pigsBag.get(i);

                p.joinedFromOtherVillage = true;

                p.sim = simBase;
                p.state = (SimState)simBase;

                simBase.pigsBag.add(p);

                p.stopper.stop();

                double interval = 1.0;
                p.stopper = simBase.schedule.scheduleRepeating(p, 3, interval);

                maxPigId++;
                p.identity = maxPigId;

                maxPigCensusId++;
                p.identity = maxPigCensusId;

            }

            simVilla.pigsBag = new Bag();

            for(int i = 0; i < simVilla.defecationSitesBag.size(); i++)
            {
                DefecationSite s = (DefecationSite)simVilla.defecationSitesBag.get(i);

                s.joinedFromOtherVillage = true;

                s.sim = simBase;
                s.state = (SimState)simBase;

                simBase.defecationSitesBag.add(s);

                s.stopper.stop();

                double interval = 1.0;
                s.stopper = simBase.schedule.scheduleRepeating(s, 12, interval);

                maxDefSId++;
                s.identity = maxDefSId;

            }

            simVilla.defecationSitesBag = new Bag();

            for(int i = 0; i < simVilla.censedPigsBag.size(); i++)
            {
                CensedPig cp = (CensedPig)simVilla.censedPigsBag.get(i);

                cp.joinedFromOtherVillage = true;

                cp.sim = simBase;
                cp.state = (SimState)simBase;

                simBase.censedPigsBag.add(cp);

            }

            simVilla.censedPigsBag = new Bag();

            //------------------------------------------------
            if(simBase.cystiHumans)
            {
                for(int i = 0; i < simVilla.humanCystsBag.size(); i++)
                {
                    HumanCyst c = (HumanCyst)simVilla.humanCystsBag.get(i);

                    c.joinedFromOtherVillage = true;

                    c.sim = simBase;
                    c.state = (SimState)simBase;

                    simBase.humanCystsBag.add(c);

                    c.stopper.stop();

                    if(c.human != null)
                    {
                        double interval = 1.0;
                        c.stopper = simBase.schedule.scheduleRepeating(c, 9, interval);
                    }

                    maxCystsId++;
                    c.identity = maxCystsId;

                }

                simVilla.humanCystsBag = new Bag();
            }
        }

        //Additional re-calculations to join the villages
        simBase.householdsGen.createHousesNeighboursMap();
        simBase.householdsGen.calcContArea();

        simBase.householdsGen.calculateHouseholdsCenterPoint();
        simBase.householdsGen.getHHsInRange();

        SimState stateTmp = (SimState)simBase;
        simBase.householdsBag.shuffle(stateTmp.random);

        simBase.countDefecationSites = new CounterDefecationSitesPigs(simBase);
        simBase.countDefecationSites.initDefecationSitesPigs();

        simBase.pigsGen.countNumberDefecationSitesAroundPigs();

        //--- read obs data for pigs 
        if(simBase.villageDataset.equals("R01"))
        {
            simBase.pigsGenR01.getPigsLinesFromFileR01();//new way of reading pigs file based on pig
                                                         //
            simBase.pigsGenR01.createCohorts();

            simBase.pigsGenR01.calculateIncidencesR01Joined();

            simBase.pigsGenR01.calculatePrevalencesByAge();

            //simBase.pigsGenR01.writePrevalencesCSV();

            //System.exit(0);

        }
        else if(simBase.villageDataset.equals("TTEMP"))
        {
            simBase.humansGen.calculatePrevalencesByAgeTTEMP();

            simBase.pigsGenTTEMP.createCohorts();
            //System.exit(0);

            simBase.pigsGenTTEMP.calculateIncidencesTTEMPJoined();

            simBase.pigsGenTTEMP.calculatePrevalencesByAge();

            //System.exit(0);
            simBase.pigsGenTTEMP.getStats_TTEMP();

            //System.exit(0);
        }


        System.out.println (" ");
        System.out.println ("--------------------------------------------------------");
        System.out.println ("Stats of the new joined village:");
        simBase.householdsGen.printHouseholdsStats();

        //System.exit(0);


    }


    //====================================================
    public void getMaxIds()
    {
        for(int i = 0; i < simBase.householdsBag.size(); i++)
        {
            Household hh = (Household)simBase.householdsBag.get(i);

            if(hh.shpId > maxShpId)maxShpId = hh.shpId;
            if(hh.simId > maxHouseholdId)maxHouseholdId = hh.simId;
        }

        for(int i = 0; i < simBase.humansBag.size(); i++)
        {
            Human h = (Human)simBase.humansBag.get(i);

            if(h.identity > maxHumanId)maxHumanId = h.identity;
        }

        for(int i = 0; i < simBase.pigsBag.size(); i++)
        {
            Pig p = (Pig)simBase.pigsBag.get(i);

            if(p.identity > maxPigId)maxPigId = p.identity;
            if(p.censusIdentity > maxPigCensusId)maxPigCensusId = p.censusIdentity;
        }

        for(int i = 0; i < simBase.defecationSitesBag.size(); i++)
        {
            DefecationSite d = (DefecationSite)simBase.defecationSitesBag.get(i);

            if(d.identity > maxDefSId)maxDefSId = d.identity;
        }

        if(simBase.cystiHumans)
        {
            for(int i = 0; i < simBase.humanCystsBag.size(); i++)
            {
                HumanCyst c = (HumanCyst)simBase.humanCystsBag.get(i);

                if(c.identity > maxCystsId)maxCystsId = c.identity;
            }

        }
    }



}


