/*
  Copyright 2011 by Francesco Pizzitutti
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.app.cystiagents;

import java.util.*;

public class CensedHouseholdsDistComparator implements Comparator, java.io.Serializable
{
   private static final long serialVersionUID = 1L;

   //----------------------------------------------------------
   public CensedHouseholdsDistComparator()
   {
   }

   //----------------------------------------------------------
   public int compare(Object ao1, Object ao2)
   {

      CensedHousehold a1 = ((CensedHousehold)ao1);
      CensedHousehold a2 = ((CensedHousehold)ao2);
 
      if(a1.distFromVillageCenter > a2.distFromVillageCenter)
          return 1;
      else if(a1.distFromVillageCenter < a2.distFromVillageCenter)
          return -1;
      else
          return 0;
   }


}
