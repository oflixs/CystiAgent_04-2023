/*
   Copyright 2011 by Francesco Pizzitutti
   Licensed under the Academic Free License version 3.0
   See the file "LICENSE" for more information
   */

package sim.app.cystiagents;

import sim.engine.*;
import sim.util.*;
import sim.util.distribution.*;
import ec.util.MersenneTwisterFast;
import java.util.ArrayList;
import java.util.List;
import java.util.*; 

import java.io.*;
import java.util.*;

import sim.util.geo.MasonGeometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Geometry;

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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Calendar;
import java.util.Locale;
import static java.util.Calendar.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;  



public class HumansGenerator implements Steppable
{
    private static final long serialVersionUID = 1L;

    public CystiAgents sim = null;
    public SimState state = null;

    public Stoppable stopper;

    public Boolean printOut = false;

    public List<List<String>> houseLines = new ArrayList<List<String>>();
    public HashMap<Integer, List<List<String>>> housesLinesMap = new HashMap<Integer, List<List<String>>>();

    public List<Double> prevalenceByAge = new ArrayList<Double>();
    public List<Double> prevalenceByAgeNorm = new ArrayList<Double>();

    //====================================================
    public HumansGenerator(final SimState pstate)
    {
        state = pstate;
        sim = (CystiAgents)state;

        double interval = 1.0;
        this.stopper = sim.schedule.scheduleRepeating(this, 0, interval);
    }

    //====================================================
    public void step(final SimState state)
    {
        if(sim.strangerTraveler)generateStrangerTraveler();
    }

    //Generate starting number of mosquitos===============
    public void generateHumans()
    {
        //CoverPixel cp = new CoverPixel(state, 1, 1);

        //if(sim.deltaLevel < 0) return;

        MersenneTwisterFast randomGenerator = new MersenneTwisterFast(System.currentTimeMillis());

        int stats = 0;
        for(int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);

            Poisson poisson = new Poisson(sim.humansPerHousehold, randomGenerator);


            int random = poisson.nextInt();
            if(random <= 0)random = 1;//no zero humans households allowed (Ian)
            //if(sim.extendedOutput)if(sim.extendedOutput)System.out.println("Poisson par, number " + sim.humansPerHousehold + " " + random);

            for(int j = 0; j < random; j++)
            {
                Human m = new Human(state, hh, 0, false, false, true);
                stats++;
            }

        }

        if(sim.extendedOutput)System.out.println(sim.villageName + ": " + stats + " Humans generated");
        //System.exit(0);


    }

    //====================================================
    public void initHumansFromDataGATES2()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ==================================================");
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ---- Initializing humans from survey data --------");
        if(sim.extendedOutput)System.out.println(" ");

        int stats = 0;

        getHousesLinesFromFileGATES2();
        //System.exit(0);

        List<String> line = new ArrayList<String>();

        int statsHumans = 0;
        int statsInf = 0;
        int statsNoInf = 0;
        int statsInfNoData = 0;
        int statsParticipated = 0;
        int statsTreatment = 0;

        //System.exit(0);


        for (int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);

            //if(sim.extendedOutput)System.out.println ("House " + hh.simId);

            houseLines = housesLinesMap.get(hh.shpId);

            //if(sim.extendedOutput)System.out.println (houseLines);

            if(houseLines == null)continue;

            for(int f = 0; f < houseLines.size(); f++)
            {
                line = (List<String>)houseLines.get(f);
                //if(sim.extendedOutput)System.out.println (line);
                //if(sim.extendedOutput)System.out.println (line.get(11));

                int infectDIA = 0;
                if(line.size() > 12)
                {
                    infectDIA = 2;
                    if(line.get(15).equals("1"))
                    {
                        infectDIA = 1;
                        statsInf++;
                    }
                    else
                    {
                        statsNoInf++;
                    }
                }
                else
                {
                    statsInfNoData++;
                }

                Human human = new Human(sim, hh, infectDIA, true, false, true);
                statsHumans++;

                human.identity = sim.humansIds;
                sim.humansIds++;

                if(line.get(0).equals("Focalizado con vacuna"))sim.village.intervention = "mass screening with vaccine";
                else if(line.get(0).equals("Focalizado sin vacuna"))sim.village.intervention = "mass screening";
                else if(line.get(0).equals("Maxima con vacuna"))sim.village.intervention = "mass treatment with vaccine";
                else if(line.get(0).equals("Maxima sin vacuna"))sim.village.intervention = "mass treatment";

                //if(sim.extendedOutput)System.out.println ("Human id " + human.identity);
                human.GATESID = line.get(6);
                if(printOut)if(sim.extendedOutput)System.out.println ("Human GATES2 ID: " + human.GATESID);

                human.age = (int)Math.round(sim.weeksInAYear * ( Integer.parseInt(line.get(6)))  );
                if(printOut)if(sim.extendedOutput)System.out.println ("New human age: " + (human.age));

                //gender
                if(line.get(7).equals("Female"))human.gender = "female";
                else human.gender = "male";
                if(printOut)if(sim.extendedOutput)System.out.println ("New Human gender: " + human.gender);

                //study participation
                if(line.get(8).equals("1"))human.studyParticipation = true;
                else human.studyParticipation = false;
                if(printOut)if(sim.extendedOutput)System.out.println ("Study participation: " + human.studyParticipation);
                if(human.studyParticipation)statsParticipated++;

                //accepted niclosamide treatment
                if(line.get(9).equals("1"))human.acceptedNMTreatment = true;
                else human.acceptedNMTreatment = false;
                if(printOut)if(sim.extendedOutput)System.out.println ("accepted niclosamide treatment: " + human.acceptedNMTreatment);
                if(human.acceptedNMTreatment)statsTreatment++;

                //To get this information you have to cross data from census 
                //and from taeniasis xls files
                //if(line.get(18).equals("1"))human.famRelation = "father";
                //else if(line.get(18).equals("2"))human.famRelation = "mather";
                //else if(line.get(18).equals("3"))human.famRelation = "child";
                //else if(line.get(18).equals("4"))human.famRelation = "grandparent";
                //else if(line.get(18).equals("5"))human.famRelation = "uncle";
                //else if(line.get(18).equals("6"))human.famRelation = "cousin";
                //else if(line.get(18).equals("7"))human.famRelation = "nephew";
                //else if(line.get(18).equals("8"))human.famRelation = "grandchild";
                //if(printOut)if(sim.extendedOutput)System.out.println ("New Human family realtion: " + human.famRelation);

            }


        }

        double dataTPrev = (double)statsInf/(double)(statsInf + statsNoInf);

        if(sim.extendedOutput)System.out.println ("Tot Humans generated: " + statsHumans);
        if(sim.extendedOutput)System.out.println ("Tot Humans infected: " + statsInf);
        if(sim.extendedOutput)System.out.println ("Human taeniasis prev: " + dataTPrev);
        //double localBaselineTnPrev = (double)statsInf/(double)(stats - statsNoData);

        if(sim.extendedOutput)System.out.println ("Tot Humans that participated to the study: " + statsParticipated);
        if(sim.extendedOutput)System.out.println ("Tot Humans that accepted niclosamide treatment " + statsTreatment);
        if(sim.extendedOutput)System.out.println ("Village intervention: " + sim.village.intervention);

        //adjustForAdditionalTaeniasis
        int additionalCases = (int)Math.round(statsInfNoData * dataTPrev);
        if(sim.extendedOutput)System.out.println ("Additional cass to be added: " + additionalCases);

        Boolean cont = true;
        if(additionalCases == 0)cont = false;
        stats = 0;
        while(cont)
        {
            int iran = state.random.nextInt(sim.humansBag.size());
            Human h = (Human)sim.humansBag.get(iran);
            if(h.tapeworm)continue;

            h.infectHumanBaseline();
            stats++;
            if(stats == additionalCases)cont = false;
        }

        if(sim.extendedOutput)System.out.println (stats + " additional cases created");

    }



    //====================================================
    public void initHumansFromDataGATES1()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ==================================================");
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ---- Initializing humans from survey data --------");
        if(sim.extendedOutput)System.out.println(" ");

        int stats = 0;

        getHousesLinesFromFileGATES1();
        //System.exit(0);

        List<String> line = new ArrayList<String>();

        int statsHumans = 0;
        int statsInf = 0;
        int statsNoInf = 0;
        int statsInfNoData = 0;
        int statsParticipated = 0;
        int statsTreatment = 0;

        //System.exit(0);


        for (int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);

            //if(sim.extendedOutput)System.out.println ("House " + hh.simId);

            houseLines = housesLinesMap.get(hh.shpId);

            //if(sim.extendedOutput)System.out.println (houseLines);

            if(houseLines == null)continue;

            for(int f = 0; f < houseLines.size(); f++)
            {
                line = (List<String>)houseLines.get(f);
                //if(sim.extendedOutput)System.out.println (line);
                //if(sim.extendedOutput)System.out.println (line.get(11));

                int infectDIA = 0;
                if(line.size() > 17)
                {
                    infectDIA = 2;
                    if(line.get(17).equals("1"))
                    {
                        infectDIA = 1;
                        statsInf++;
                    }
                    else
                    {
                        statsNoInf++;
                    }
                }
                else
                {
                    statsInfNoData++;
                }

                Human human = new Human(sim, hh, infectDIA, true, false, true);
                statsHumans++;

                human.identity = sim.humansIds;
                sim.humansIds++;

                if(line.get(0).equals("6"))sim.village.intervention = "pigs replacement";

                //if(sim.extendedOutput)System.out.println ("Human id " + human.identity);
                human.GATESID = line.get(6);
                if(printOut)if(sim.extendedOutput)System.out.println ("Human GATES2 ID: " + human.GATESID);

                human.age = (int)Math.round(sim.weeksInAYear * ( Integer.parseInt(line.get(5)))  );
                if(printOut)if(sim.extendedOutput)System.out.println ("New human age: " + (human.age));

                //gender
                if(line.get(4).equals("0"))human.gender = "female";
                else human.gender = "male";
                if(printOut)if(sim.extendedOutput)System.out.println ("New Human gender: " + human.gender);

                //study participation
                //if(line.get(8).equals("1"))human.studyParticipation = true;
                //else human.studyParticipation = false;
                //if(printOut)if(sim.extendedOutput)System.out.println ("Study participation: " + human.studyParticipation);
                //if(human.studyParticipation)statsParticipated++;

                //accepted niclosamide treatment
                if(line.get(13).equals("1"))human.acceptedNMTreatment = true;
                else human.acceptedNMTreatment = false;
                if(printOut)if(sim.extendedOutput)System.out.println ("accepted niclosamide treatment: " + human.acceptedNMTreatment);
                if(human.acceptedNMTreatment)statsTreatment++;

                //To get this information you have to cross data from census 
                //and from taeniasis xls files
                //if(line.get(18).equals("1"))human.famRelation = "father";
                //else if(line.get(18).equals("2"))human.famRelation = "mather";
                //else if(line.get(18).equals("3"))human.famRelation = "child";
                //else if(line.get(18).equals("4"))human.famRelation = "grandparent";
                //else if(line.get(18).equals("5"))human.famRelation = "uncle";
                //else if(line.get(18).equals("6"))human.famRelation = "cousin";
                //else if(line.get(18).equals("7"))human.famRelation = "nephew";
                //else if(line.get(18).equals("8"))human.famRelation = "grandchild";
                //if(printOut)if(sim.extendedOutput)System.out.println ("New Human family realtion: " + human.famRelation);

            }


        }

        double dataTPrev = (double)statsInf/(double)(statsInf + statsNoInf);

        if(sim.extendedOutput)System.out.println ("Tot Humans generated: " + statsHumans);
        if(sim.extendedOutput)System.out.println ("Tot Humans infected: " + statsInf);
        if(sim.extendedOutput)System.out.println ("Human taeniasis prev: " + dataTPrev);
        //double localBaselineTnPrev = (double)statsInf/(double)(stats - statsNoData);

        if(sim.extendedOutput)System.out.println ("Tot Humans that participated to the study: " + statsParticipated);
        if(sim.extendedOutput)System.out.println ("Tot Humans that accepted niclosamide treatment " + statsTreatment);
        if(sim.extendedOutput)System.out.println ("Village intervention: " + sim.village.intervention);

        //adjustForAdditionalTaeniasis
        int additionalCases = (int)Math.round(statsInfNoData * dataTPrev);
        if(sim.extendedOutput)System.out.println ("Additional cass to be added: " + additionalCases);

        Boolean cont = true;
        if(additionalCases == 0)cont = false;
        stats = 0;
        while(cont)
        {
            int iran = state.random.nextInt(sim.humansBag.size());
            Human h = (Human)sim.humansBag.get(iran);
            if(h.tapeworm)continue;

            h.infectHumanBaseline();
            stats++;
            if(stats == additionalCases)cont = false;
        }

        if(sim.extendedOutput)System.out.println (stats + " additional cases created");

    }

    //====================================================
    public void getHousesLinesFromFileGATES2()
    {
        String inputFile = "";
        String sheetName = "";
        inputFile = "./inputData/populationsData/GATES2/Gates 2_Baseline taeniasis_04102020.xls";
        sheetName = "Gates 2 baseline humans";

        try{

            Workbook workbookFile = WorkbookFactory.create(new FileInputStream(inputFile));
            //XSSFWorkbook workbookFile = new XSSFWorkbook(new FileInputStream(ext.filePriorABC));

            Sheet sheet = workbookFile.getSheet(sheetName);

            int statsRows = -1;
            int statsCells = -1;
            int lastCellNum = 0;
            int p = 0;
            int m = 0;

            boolean startRead = false;
            boolean stopRead = false;
            houseLines = new ArrayList<List<String>>();
            List<String> line = new ArrayList<String>();
rows:             
            for(Row row : sheet)
            { 
                statsRows++;
                //if(statsRows == 0)continue;
                //if(sim.extendedOutput)System.out.println ("nrow: " + statsRows);

                int stats = -1;
                Boolean read = false;

                line = new ArrayList<String>();

                //for(Cell cell : row)
                for (int i = 0; i < row.getLastCellNum(); i++) 
                {  
                    Cell cell = row.getCell(i);
                    statsCells++;

                    String stri = "";

                    if(cell == null)
                    {
                        stri = "";
                    }
                    else
                    {

                        switch (cell.getCellType()) 
                        {
                            case Cell.CELL_TYPE_BLANK:
                                stri = "";
                                //if(sim.extendedOutput)System.out.println ("dsadadsasd");
                            case Cell.CELL_TYPE_STRING:
                                stri = cell.getRichStringCellValue().getString(); 
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");  
                                    stri = dateFormat.format(date);  
                                }
                                else
                                {
                                    double d = (double)cell.getNumericCellValue();
                                    int aaa = (int)Math.round(d);
                                    stri = Integer.toString(aaa);
                                }
                                break;
                            default:
                                stri = cell.getRichStringCellValue().getString(); 
                                break;
                        }
                    }

                    line.add(stri);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                if(line.get(2).equals(sim.villageNameNumber))
                {
                    int houseNum = Integer.parseInt(line.get(3));

                    if(housesLinesMap.containsKey(houseNum))
                    {
                        houseLines = housesLinesMap.get(houseNum);
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }
                    else
                    {
                        houseLines = new ArrayList<List<String>>();
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }

                }

            }

        }
        catch(FileNotFoundException e)
        {
            System.out.println(e);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        catch(InvalidFormatException e)
        {
            System.out.println(e);
        }




    }



    //====================================================
    public void getHousesLinesFromFileGATES1()
    {
        String inputFile = "";
        String sheetName = "";
        inputFile = "./inputData/populationsData/GATES1/Gates1_Humans_Rd1_2005.xls";
        sheetName = "Gates1 Humans Rd1";

        try{

            Workbook workbookFile = WorkbookFactory.create(new FileInputStream(inputFile));
            //XSSFWorkbook workbookFile = new XSSFWorkbook(new FileInputStream(ext.filePriorABC));

            Sheet sheet = workbookFile.getSheet(sheetName);

            int statsRows = -1;
            int statsCells = -1;
            int lastCellNum = 0;
            int p = 0;
            int m = 0;

            boolean startRead = false;
            boolean stopRead = false;
            houseLines = new ArrayList<List<String>>();
            List<String> line = new ArrayList<String>();
rows:             
            for(Row row : sheet)
            { 
                statsRows++;
                //if(statsRows == 0)continue;
                //if(sim.extendedOutput)System.out.println ("nrow: " + statsRows);

                int stats = -1;
                Boolean read = false;

                line = new ArrayList<String>();

                //for(Cell cell : row)
                for (int i = 0; i < row.getLastCellNum(); i++) 
                {  
                    Cell cell = row.getCell(i);
                    statsCells++;

                    String stri = "";

                    if(cell == null)
                    {
                        stri = "";
                    }
                    else
                    {

                        switch (cell.getCellType()) 
                        {
                            case Cell.CELL_TYPE_BLANK:
                                stri = "";
                                //if(sim.extendedOutput)System.out.println ("dsadadsasd");
                            case Cell.CELL_TYPE_STRING:
                                stri = cell.getRichStringCellValue().getString(); 
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");  
                                    stri = dateFormat.format(date);  
                                }
                                else
                                {
                                    double d = (double)cell.getNumericCellValue();
                                    int aaa = (int)Math.round(d);
                                    stri = Integer.toString(aaa);
                                }
                                break;
                            default:
                                stri = cell.getRichStringCellValue().getString(); 
                                break;
                        }
                    }

                    line.add(stri);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                if(line.get(1).equals(sim.villageNameNumber))
                {
                    int houseNum = Integer.parseInt(line.get(2));

                    if(housesLinesMap.containsKey(houseNum))
                    {
                        houseLines = housesLinesMap.get(houseNum);
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }
                    else
                    {
                        houseLines = new ArrayList<List<String>>();
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }

                }

            }

        }
        catch(FileNotFoundException e)
        {
            System.out.println(e);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        catch(InvalidFormatException e)
        {
            System.out.println(e);
        }




    }

    //====================================================
    public void getNumActiveDefecationSites()
    {
        sim.numActiveDefecationSites = 0.0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human h = (Human)sim.humansBag.get(i);

            if(h.sewerUser)continue;

            if(h.latrineUser)sim.numActiveDefecationSites = sim.numActiveDefecationSites - (1 - (sim.adherenceToLatrineUse * h.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
            else sim.numActiveDefecationSites = sim.numActiveDefecationSites - sim.propDefInsideRadius;

        }
        //if(sim.extendedOutput)System.out.println ("Num active defecation sites: " + sim.numActiveDefecationSites);

    }

    //====================================================
    public void initHumansFromDataTTEMP()
    {
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ==================================================");
        if(sim.extendedOutput)System.out.println(sim.villageName  + " ---- Initializing humans from survey data --------");
        if(sim.extendedOutput)System.out.println(" ");

        int stats = 0;

        getHousesLinesFromFileTTEMP();

        List<String> line = new ArrayList<String>();

        //System.exit(0);

        int statsNoData = 0;
        int statsInf = 0;
        for (int i = 0; i < sim.householdsBag.size(); i++)
        {
            Household hh = (Household)sim.householdsBag.get(i);

            //if(sim.extendedOutput)System.out.println ("House " + hh.simId);

            houseLines = housesLinesMap.get(hh.shpId);

            if(houseLines == null)continue;

            for(int f = 0; f < houseLines.size(); f++)
            {
                line = (List<String>)houseLines.get(f);

                //if(sim.extendedOutput)System.out.println (line.get(15));

                //infection state
                int infectDIA = 2;
                if(line.get(15).equals("1"))
                {
                    infectDIA = 1;//human with taeniasis
                    statsInf++;
                    hh.numInfHumans++;
                }
                if(line.get(15).equals("0"))infectDIA = 2;//human no infected
                if(line.get(15).equals(""))
                    //adds additional infected human to the household
                {
                    statsNoData++;
                    infectDIA = 0;//human with not data will be infected
                }
                //following village baseline prevalence

                Human human = new Human(sim, hh, infectDIA, true, false, true);
                stats++;

                human.identity = sim.humansIds;
                sim.humansIds++;

                //gender
                if(line.get(4).equals("0"))human.gender = "female";
                else human.gender = "male";
                if(printOut)if(sim.extendedOutput)System.out.println ("New Human gender: " + human.gender);

                if(line.get(15).equals("1"))
                {
                    if(human.gender.equals("male"))hh.numInfectedMale++;
                    else hh.numInfectedFemale++;
                }


                //if(sim.extendedOutput)System.out.println ("Human id " + human.identity);

                human.age = (int)Math.round(sim.weeksInAYear * ( Integer.parseInt(line.get(5))) );
                if(printOut)if(sim.extendedOutput)System.out.println ("New human age: " + (human.age));

                if(line.get(7).equals("1"))human.famRelation = "father";
                else if(line.get(7).equals("2"))human.famRelation = "mather";
                else if(line.get(7).equals("3"))human.famRelation = "child";
                else if(line.get(7).equals("4"))human.famRelation = "grandparent";
                else if(line.get(7).equals("5"))human.famRelation = "uncle";
                else if(line.get(7).equals("6"))human.famRelation = "cousin";
                else if(line.get(7).equals("7"))human.famRelation = "nephew";
                else if(line.get(7).equals("8"))human.famRelation = "grandchild";
                else if(line.get(7).equals("9"))human.famRelation = "brother/sister";
                else if(line.get(7).equals("9"))human.famRelation = "service personnel";
                if(printOut)if(sim.extendedOutput)System.out.println ("New Human family realtion: " + human.famRelation);

            }
        }

        if(sim.extendedOutput)System.out.println ("Tot Humans generated: " + stats);
        if(sim.extendedOutput)System.out.println ("Human taeniasis prev: " + (double)statsInf/(double)(stats - statsNoData));
        double localBaselineTnPrev = (double)statsInf/(double)(stats - statsNoData);

        //System.exit(0);

    }

    //-----------------------------------------------
    public void calculatePrevalencesByAgeTTEMP()
    {
        int maxAge = (int)Math.round(120 * sim.weeksInAYear);
        for(int i = 0; i < maxAge; i++)
        {
            prevalenceByAge.add(0.0);
            prevalenceByAgeNorm.add(0.0);
        }

        int stats = 0;

        for(int i = 0; i < sim.humansBag.size(); i++)
        {
            Human human = (Human)sim.humansBag.get(i);

            //if(!cp.roundsVillages.get(i).equals(sim.villageNameNumber))continue;
            //if(i != 6)continue;

            prevalenceByAgeNorm.set(human.age, (prevalenceByAgeNorm.get(human.age) + 1.0));
            if(human.tapeworm)prevalenceByAge.set(human.age, (prevalenceByAge.get(human.age) + 1.0));
            stats++;
        }



        //normalize
        List<Double> positives = new ArrayList<Double>(prevalenceByAge);
        double statsTot = 0;
        for(int i = 0; i < maxAge; i++)
        {
            prevalenceByAge.set(i, (prevalenceByAge.get(i)/prevalenceByAgeNorm.get(i)));
            statsTot = statsTot + prevalenceByAgeNorm.get(i);
        }

        if(sim.extendedOutput)System.out.println(sim.villageName + " ");
        if(sim.extendedOutput)System.out.println(sim.villageName + "---- Taeniais prevalence by human age:");
        if(sim.extendedOutput)System.out.println("age-in-weeks age-in-years num-humans-per-age-segment num-positive-humans-per-age-segment prop-humans-per-age-segment prevalence-humans-per-age-segment" );
        for(int i = 0; i < maxAge; i++)
        {
            if(prevalenceByAgeNorm.get(i) > 0)
            {
                if(sim.extendedOutput)System.out.println(i 
                        + " " + i / sim.weeksInAYear
                        + " " + prevalenceByAgeNorm.get(i) 
                        + " " + positives.get(i) 
                        + " " + prevalenceByAgeNorm.get(i)/(double)statsTot
                        + " " + prevalenceByAge.get(i));
            }
        }

        //System.exit(0);
    }



    //====================================================
    public void getHousesLinesFromFileTTEMP()
    {
        String inputFile = "";
        String sheetName = "";
        inputFile = "./inputData/populationsData/TTEMP/TTEMP_Humans_2015.xls";
        sheetName = "TTEMP Humans 2015";

        try{

            Workbook workbookFile = WorkbookFactory.create(new FileInputStream(inputFile));
            //XSSFWorkbook workbookFile = new XSSFWorkbook(new FileInputStream(ext.filePriorABC));

            Sheet sheet = workbookFile.getSheet(sheetName);

            int statsRows = -1;
            int statsCells = -1;
            int lastCellNum = 0;
            int p = 0;
            int m = 0;

            boolean startRead = false;
            boolean stopRead = false;
            houseLines = new ArrayList<List<String>>();
            List<String> line = new ArrayList<String>();
rows:             
            for(Row row : sheet)
            { 
                statsRows++;
                //if(statsRows == 0)continue;
                //if(sim.extendedOutput)System.out.println ("nrow: " + statsRows);

                int stats = -1;
                Boolean read = false;

                line = new ArrayList<String>();

                for(Cell cell : row)
                {  
                    statsCells++;

                    String stri = "";

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");  
                                stri = dateFormat.format(date);  
                            }
                            else
                            {
                                double d = (double)cell.getNumericCellValue();
                                int aaa = (int)Math.round(d);
                                stri = Integer.toString(aaa);
                            }
                            break;
                        default:
                            stri = cell.getRichStringCellValue().getString(); 
                            break;
                    }
                    line.add(stri);
                }

                //if(sim.extendedOutput)System.out.println ("line 2: " + line.get(2));
                if(line.get(1).equals(sim.villageNameNumber))
                {
                    int houseNum = Integer.parseInt(line.get(2));

                    if(housesLinesMap.containsKey(houseNum))
                    {
                        houseLines = housesLinesMap.get(houseNum);
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }
                    else
                    {
                        houseLines = new ArrayList<List<String>>();
                        houseLines.add(line);
                        housesLinesMap.put(houseNum, houseLines);
                    }

                }

            }

        }
        catch(FileNotFoundException e)
        {
            System.out.println(e);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        catch(InvalidFormatException e)
        {
            System.out.println(e);
        }
    }

    //====================================================
    public void generateStrangerTraveler()
    {
        //the frequency of strange traveler arrival is set to be equivalent
        //to the number of travels the population of the village 
        //is generated to the village outside each week

        double freq = 1.0/sim.travelFreq;
        freq = freq * sim.strangerTravelersProp;

        //if(sim.extendedOutput)System.out.println ("------------ freq: " + freq);
        // if(sim.extendedOutput)System.out.println ("freq: " + freq);

        //if(sim.extendedOutput)System.out.println ("numTravelers: " + numTravelers);

        int size = (int)Math.round(sim.householdsBag.size() * sim.travelerProp);

        for(int i = 0; i < size; i++)
        {
            double random = state.random.nextDouble();

            if(random >= freq)return;

            int hIndex = state.random.nextInt(sim.householdsBag.size() - 1);
            Household hh = (Household)sim.householdsBag.get(hIndex);

            Human human = new Human(sim, hh, 0, false, true, true);

            if(!human.sewerUser)
            {
                if(human.latrineUser)sim.numActiveDefecationSites = sim.numActiveDefecationSites + (1 - (sim.adherenceToLatrineUse * human.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                else sim.numActiveDefecationSites = sim.numActiveDefecationSites + sim.propDefInsideRadius;
            }

            //if(sim.extendedOutput)System.out.println ("strangerTraveler: " + human.identity);

            sim.strangerTravelersBag.add(human);

            human.travelDuration = (int)Math.ceil(-sim.travelDuration * Math.log(random));
            if(human.travelDuration < 1)human.travelDuration = 2;
            //if(sim.extendedOutput)System.out.println("travel duration: " + human.travelDuration);

            addToPigsDefecatorsList(human);

            //if(!sim.burnin && human.tapeworm)
            //{
            //    if(sim.extendedOutput)System.out.println("Infected traveler !!!!!!!!!!!!!!!!!!!!!!");
            //}
            //if(!sim.burnin && human.tapewormMature)
            //{
            //    if(sim.extendedOutput)System.out.println("Infected traveler tapewormMature !!!!!!!!!!!!!!!!!!!!!!");
            //}
        }

    }

    //====================================================
    public void generateStrangerTravelerOld()
    {
        double random = state.random.nextDouble();
        //the frequency of strange traveler arrival is set to be equivalent
        //to the number of travels the population of the village 
        //is generated to the village outside each week
        double freq = sim.travelerProp * (double)sim.householdsBag.size()/(double)sim.travelFreq;

        if(sim.extendedOutput)System.out.println ("------------ freq: " + freq);

        // if(sim.extendedOutput)System.out.println ("freq: " + freq);

        int numTravelers = (int)Math.ceil(freq);

        numTravelers = state.random.nextInt(numTravelers);

        if(numTravelers <= 0)return;

        if(sim.extendedOutput)System.out.println ("numTravelers: " + numTravelers);

        for(int i = 0; i < numTravelers; i++)
        {
            int hIndex = state.random.nextInt(sim.householdsBag.size() - 1);
            Household hh = (Household)sim.householdsBag.get(hIndex);

            Human human = new Human(sim, hh, 0, false, true, true);

            if(!human.sewerUser)
            {
                if(human.latrineUser)sim.numActiveDefecationSites = sim.numActiveDefecationSites + (1 - (sim.adherenceToLatrineUse * human.adherenceToLatrineBorderEffect)) * sim.propDefInsideRadius;
                else sim.numActiveDefecationSites = sim.numActiveDefecationSites + sim.propDefInsideRadius;
            }

            if(sim.extendedOutput)System.out.println ("strangerTraveler: " + human.identity);

            sim.strangerTravelersBag.add(human);

            human.travelDuration = (int)Math.ceil(-sim.travelDuration * Math.log(random));
            if(human.travelDuration < 1)human.travelDuration = 2;
            //if(sim.extendedOutput)System.out.println("travel duration: " + human.travelDuration);

            addToPigsDefecatorsList(human);

            //if(!sim.burnin && human.tapeworm)
            //{
            //    if(sim.extendedOutput)System.out.println("Infected traveler !!!!!!!!!!!!!!!!!!!!!!");
            //}
            //if(!sim.burnin && human.tapewormMature)
            //{
            //    if(sim.extendedOutput)System.out.println("Infected traveler tapewormMature !!!!!!!!!!!!!!!!!!!!!!");
            //}
        }

    }


    //====================================================
    public void addToPigsDefecatorsList(Human human)
    {
        if(!sim.useLandCover)
        {
            for(int i = 0; i < sim.pigsBag.size(); i++)
            {
                Pig pig = (Pig)sim.pigsBag.get(i);

                //CoverPixel cp = human.cpPositionDefecationSite;

                //double dist = (cp.xcor - pig.cpPosition.xcor) * (cp.xcor - pig.cpPosition.xcor);
                //dist = dist + (cp.ycor - pig.cpPosition.ycor) * (cp.ycor - pig.cpPosition.ycor);
                //dist = Math.sqrt(dist);
                //dist = dist * sim.geoCellSize;

                Point hPoint = human.household.geoPoint;
                Point pPoint = pig.household.geoPoint;

                //double dist = (cp.xcor - pig.cpPosition.xcor) * (cp.xcor - pig.cpPosition.xcor);
                //dist = dist + (cp.ycor - pig.cpPosition.ycor) * (cp.ycor - pig.cpPosition.ycor);

                //double dist = (hPoint.getX() - pPoint.getX()) * (hPoint.getX() - pPoint.getX());
                //dist = dist + (hPoint.getY() - pPoint.getY()) * (hPoint.getY() - pPoint.getY());
                //dist = Math.sqrt(dist);

                double dist = hPoint.distance(pPoint);

                if(dist <= pig.homeRange)
                {
                    if(!human.sewerUser)
                    {
                        if(human.latrineUser)pig.numDefecationSitesInHomeRange 
                            = pig.numDefecationSitesInHomeRange + (1 - sim.adherenceToLatrineUse * human.adherenceToLatrineBorderEffect) * sim.propDefInsideRadius;
                        else pig.numDefecationSitesInHomeRange = pig.numDefecationSitesInHomeRange + sim.propDefInsideRadius;
                        pig.defecatorsInRangeList.add(human);
                    }
                }
            }
        }
        else
        {
            for(int i = 0; i < sim.pigsBag.size(); i++)
            {
                Pig pig = (Pig)sim.pigsBag.get(i);

                if(pig.homeAreaShape.contains((Geometry)human.defecationSite.pointPos))
                {
                    if(!human.sewerUser)
                    {
                        if(human.latrineUser)pig.numDefecationSitesInHomeRange 
                            = pig.numDefecationSitesInHomeRange + (1 - sim.adherenceToLatrineUse * human.adherenceToLatrineBorderEffect) * sim.propDefInsideRadius;
                        else pig.numDefecationSitesInHomeRange = pig.numDefecationSitesInHomeRange + sim.propDefInsideRadius;
                        pig.defecatorsInRangeList.add(human);
                    }
                }

            }
        }
    }




    //============================================================   
}
