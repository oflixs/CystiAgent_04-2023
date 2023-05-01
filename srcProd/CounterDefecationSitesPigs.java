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
import com.vividsolutions.jts.geom.Geometry;

//----------------------------------------------------
public class CounterDefecationSitesPigs implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public Stoppable stopper;

    //boolean tagged = false;

    //====================================================
    public CounterDefecationSitesPigs(SimState pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;

        double interval = 1.0;
        this.stopper = sim.schedule.scheduleRepeating(this, 13, interval);

        //setupCountDef();

    }

    //====================================================
    public void step(SimState state)
    {
        initDefecationSitesPigs();
        //countPigs was a code bottle neck
        //all those countPigs differ for computation speed being countPigs3 the fastest
        //countPigs4();
        if(!sim.useLandCover)
        {
            countPigs3();
        }
        else
        {
            countPigs3LandCover();
        }

        countDefecationSites();
    }

    //====================================================
    public void setupCountDef()
    {

        double propInside = 0.0;
        int stats = 0;

        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household h1 = (Household)sim.householdsBag.get(i);
            stats = 0;

            for(int j = 0; j < sim.householdsBag.size(); j++)
            {
                Household h2 = (Household)sim.householdsBag.get(j);

                Point p1 = h1.geoPoint;
                Point p2 = h2.geoPoint;

                //double dist = (h1.cpPosition.xcor - h2.cpPosition.xcor) * (h1.cpPosition.xcor - h2.cpPosition.xcor);
                //dist = dist + (h1.cpPosition.ycor - h2.cpPosition.ycor) * (h1.cpPosition.ycor - h2.cpPosition.ycor);
                //dist = Math.sqrt(dist);
                //dist = dist * sim.geoCellSize;

                double dist = p1.distance(p2);

                double deltah = h1.contRadiusHh; 
                double deltap = Math.exp(sim.homeRangeMean + 3 * sim.homeRangeSd);
                double maxDist = deltah + deltap;

                //System.out.println("Dist: " + dist + " maDist: " + maxDist);
                if(dist < maxDist)
                {
                    h1.toCountDefecationSites.add(h2);
                    stats++;
                    //System.out.println("in");
                }
                else
                {
                    //System.out.println("out");
                }

            }

            propInside = propInside + (double)stats/(double)sim.householdsBag.size();
        }

        System.out.println("cwnum houses: " + sim.householdsBag.size());
        System.out.println("prop inside: " + propInside/(double)sim.householdsBag.size());


        System.exit(0);
    }

    //====================================================
    public void initDefecationSitesPigs()
    {
        //System.out.println("---- CounterDefecationSites step");
        sim.defecationSitesPigs = new HashMap <DefecationSite, List<Pig>>();

        //init eggsBag
        //int stats = 0;
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);
            sim.defecationSitesPigs.put(h.defecationSite, null);
            //stats++;
        }
        //System.out.println("Num Eggs from counter: " + stats);
    }

    /*
    //====================================================
    public void countPigs()
    {
        for(int p = 0; p < sim.pigsBag.size(); p++)
        {
            Pig pig = (Pig)sim.pigsBag.get(p);
            CoverPixel cpPig = pig.cpPosition;

            double rand = state.random.nextDouble();
            if(pig.corraled.equals("never") 
                    || (pig.corraled.equals("sometimes") &&  rand > sim.propCorralSometimes)
                    )
            {
                pig.isCorraled = false;
            }
            else pig.isCorraled = true;

            //if(pig.corraled.equals("sometimes"))
            //{
            //    System.out.println("pig corraled sometimes");
            //}


            if(pig.isCorraled)continue;

            List <Pig> pigList = new ArrayList <Pig>();

            for(int i = 0; i < sim.humansBag.size(); i++)
            {
                Human h = (Human)sim.humansBag.get(i);
                CoverPixel cpDefecationSite = h.defecationSite.cpPosition;

                double dist = (cpDefecationSite.xcor - cpPig.xcor) * (cpDefecationSite.xcor - cpPig.xcor);
                dist = dist + (cpDefecationSite.ycor - cpPig.ycor) * (cpDefecationSite.ycor - cpPig.ycor);
                dist = Math.sqrt(dist);
                dist = dist * sim.geoCellSize;
                //System.out.println("pig homerange: " + pig.homeRange);
                if(dist <= pig.homeRange)
                {
                    pigList = sim.defecationSitesPigs.get(h.defecationSite);
                    if(pigList == null)pigList = new ArrayList <Pig>();
                    pigList.add(pig);
                    sim.defecationSitesPigs.put(h.defecationSite, pigList);
                }
            }
        }
    }
    */

    //====================================================
    public void countPigs3()
    {
        //-- Super optimized to increase speed

        //if(pig.corraled.equals("sometimes"))
        //{
        //    System.out.println("pig corraled sometimes");
        //}

        Bag noCorralPigs = new Bag();

        for(int p = 0; p < sim.pigsBag.size(); p++)
        {
            Pig pig = (Pig)sim.pigsBag.get(p);

            double rand = state.random.nextDouble();
            if(pig.corraled.equals("never") 
                    || (pig.corraled.equals("sometimes") &&  rand > sim.propCorralSometimes)
              )
            {
                pig.isCorraled = false;
                noCorralPigs.add(pig);
            }
            else pig.isCorraled = true;
        }

        List <Pig> pigList = new ArrayList <Pig>();
        List <Pig> pigListOld = new ArrayList <Pig>();

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            if(h.sewerUser)continue;

            if(!h.defecationSite.eggs && !h.defecationSite.proglottid)continue;

            //CoverPixel cpDefecationSite = h.defecationSite.cpPosition;

            Point defPoint = h.defecationSite.pointPos;

            pigList = sim.defecationSitesPigs.get(h.defecationSite);
            if(pigList == null)pigList = new ArrayList <Pig>();
            sim.defecationSitesPigs.put(h.defecationSite, pigList);

            for(int p = 0; p < noCorralPigs.size(); p++)
            {
                Pig pig = (Pig)noCorralPigs.get(p);

                //if(pigList.contains(pig))

                //CoverPixel cpPig = pig.cpPosition;

                Point pPoint = pig.household.geoPoint;

                //double dist = (cpDefecationSite.xcor - cpPig.xcor) * (cpDefecationSite.xcor - cpPig.xcor);
                //dist = dist + (cpDefecationSite.ycor - cpPig.ycor) * (cpDefecationSite.ycor - cpPig.ycor);
                //dist = Math.sqrt(dist);
                //dist = dist * sim.geoCellSize;

                //double dist = (defPoint.getX() - pPoint.getX()) * (defPoint.getX() - pPoint.getX());
                //dist = dist + (defPoint.getY() - pPoint.getY()) * (defPoint.getY() - pPoint.getY());
                //dist = Math.sqrt(dist);
                //dist = dist * sim.geocellsize;


                double dist = defPoint.distance(pPoint);

                //System.out.println("pig homerange: " + pig.homeRange);
                if(dist <= pig.homeRange)
                {
                    pigList.add(pig);
                }
            }
        }
    }

    /*
    //====================================================
    public void countPigs4()
    {
        //if(pig.corraled.equals("sometimes"))
        //{
        //    System.out.println("pig corraled sometimes");
        //}

        Bag noCorralPigs = new Bag();
        int stats = 0;

        //System.out.println("-----------");
        for(int p = 0; p < sim.pigsBag.size(); p++)
        {
            Pig pig = (Pig)sim.pigsBag.get(p);

            double rand = state.random.nextDouble();
            if(pig.corraled.equals("never") 
                    || (pig.corraled.equals("sometimes") &&  rand > sim.propCorralSometimes)
              )
            {
                pig.isCorraled = false;
                noCorralPigs.add(pig);
            }
            else
            {
                pig.isCorraled = true;
                //stats++;
                //System.out.println("pig corraled: " + stats);
            }

        }

        List <Pig> pigList = new ArrayList <Pig>();
        List <Pig> pigListOld = new ArrayList <Pig>();

        double deltap = Math.exp(sim.homeRangeMean + 3 * sim.homeRangeSd);

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);
            CoverPixel cpDefecationSite = h.defecationSite.cpPosition;

            pigList = sim.defecationSitesPigs.get(h.defecationSite);
            if(pigList == null)pigList = new ArrayList <Pig>();
            sim.defecationSitesPigs.put(h.defecationSite, pigList);


            for(int j = 0; j < sim.householdsBag.size(); j++)
            {
                Household hh = (Household)sim.householdsBag.get(j);

                double deltah = hh.contRadiusHh; 
                double maxDist = deltah + deltap;

                double dist = (hh.cpPosition.xcor - h.cpPosition.xcor) * (hh.cpPosition.xcor - h.cpPosition.xcor);
                dist = dist + (hh.cpPosition.ycor - h.cpPosition.ycor) * (hh.cpPosition.ycor - h.cpPosition.ycor);
                dist = Math.sqrt(dist);
                dist = dist * sim.geoCellSize;
                //System.out.println("pig homerange: " + pig.homeRange);
                if(dist > maxDist)continue;

                for(int p = 0; p < hh.pigs.size(); p++)
                {
                    Pig pig = (Pig)hh.pigs.get(p);

                    if(pig.isCorraled)continue;

                    //if(pigList.contains(pig))

                    CoverPixel cpPig = pig.cpPosition;

                    dist = (cpDefecationSite.xcor - cpPig.xcor) * (cpDefecationSite.xcor - cpPig.xcor);
                    dist = dist + (cpDefecationSite.ycor - cpPig.ycor) * (cpDefecationSite.ycor - cpPig.ycor);
                    dist = Math.sqrt(dist);
                    dist = dist * sim.geoCellSize;
                    //System.out.println("pig homerange: " + pig.homeRange);
                    if(dist <= pig.homeRange)
                    {
                        pigList.add(pig);
                    }
                }

            }


        }
    }
    */

    //====================================================
    public void countDefecationSites()
    {
        sim.eggs = 0;
        sim.proglottid = 0;
        sim.numContaminatedSites = 0;
        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            if(h.sewerUser)continue;
            
            if(h.defecationSite.eggs || h.defecationSite.proglottid)sim.numContaminatedSites++;

            if(!h.latrineUser)
            {
                if(h.defecationSite.eggs)sim.eggs = sim.eggs + sim.propDefInsideRadius;
                if(h.defecationSite.proglottid)sim.proglottid = sim.proglottid + sim.propDefInsideRadius;
            }
            else
            {
                if(h.defecationSite.eggs)sim.eggs = sim.eggs + (1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                if(h.defecationSite.proglottid)sim.proglottid = sim.proglottid + (1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
            }
        }
    }

    /*
    //====================================================
    public void countPigs2()
    {
        for(int p = 0; p < sim.pigsBag.size(); p++)
        {
            Pig pig = (Pig)sim.pigsBag.get(p);
            CoverPixel cpPig = pig.cpPosition;

            double rand = state.random.nextDouble();
            if(pig.corraled.equals("never") 
                    || (pig.corraled.equals("sometimes") &&  rand > sim.propCorralSometimes)
              )
            {
                pig.isCorraled = false;
            }
            else pig.isCorraled = true;

            //if(pig.corraled.equals("sometimes"))
            //{
            //    System.out.println("pig corraled sometimes");
            //}


            if(pig.isCorraled)continue;

            List <Pig> pigList = new ArrayList <Pig>();


            for(int i = 0; i < pig.household.toCountDefecationSites.size(); i++)
            {
                Household hh = (Household)pig.household.toCountDefecationSites.get(i);

                for(int j = 0; j < hh.humans.size(); j++)
                {
                    Human h = (Human)hh.humans.get(j);
                    CoverPixel cpDefecationSite = h.defecationSite.cpPosition;

                    double dist = (cpDefecationSite.xcor - cpPig.xcor) * (cpDefecationSite.xcor - cpPig.xcor);
                    dist = dist + (cpDefecationSite.ycor - cpPig.ycor) * (cpDefecationSite.ycor - cpPig.ycor);
                    dist = Math.sqrt(dist);
                    dist = dist * sim.geoCellSize;
                    //System.out.println("pig homerange: " + pig.homeRange);
                    if(dist <= pig.homeRange)
                    {
                        pigList = sim.defecationSitesPigs.get(h.defecationSite);
                        if(pigList == null)pigList = new ArrayList <Pig>();
                        pigList.add(pig);
                        sim.defecationSitesPigs.put(h.defecationSite, pigList);
                    }
                }
            }
        }
    }
    */

    //====================================================
    public void countPigs3LandCover()
    {
        //-- Super optimized to increase speed

        //if(pig.corraled.equals("sometimes"))
        //{
        //    System.out.println("pig corraled sometimes");
        //}

        Bag noCorralPigs = new Bag();

        for(int p = 0; p < sim.pigsBag.size(); p++)
        {
            Pig pig = (Pig)sim.pigsBag.get(p);

            double rand = state.random.nextDouble();
            if(pig.corraled.equals("never") 
                    || (pig.corraled.equals("sometimes") &&  rand > sim.propCorralSometimes)
              )
            {
                pig.isCorraled = false;
                noCorralPigs.add(pig);
            }
            else pig.isCorraled = true;
        }

        List <Pig> pigList = new ArrayList <Pig>();
        List <Pig> pigListOld = new ArrayList <Pig>();

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            if(h.sewerUser)continue;

            if(!h.defecationSite.eggs && !h.defecationSite.proglottid)continue;

            //CoverPixel cpDefecationSite = h.defecationSite.cpPosition;

            pigList = sim.defecationSitesPigs.get(h.defecationSite);
            //pitxi
            if(pigList == null)
            {
                pigList = new ArrayList <Pig>();
                sim.defecationSitesPigs.put(h.defecationSite, pigList);
            }

            for(int p = 0; p < noCorralPigs.size(); p++)
            {
                Pig pig = (Pig)noCorralPigs.get(p);

                if(pig.homeAreaShape.contains((Geometry)h.defecationSite.pointPos))
                {
                    pigList.add(pig);
                }
            }
        }
    }



}


