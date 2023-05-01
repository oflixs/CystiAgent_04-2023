/*
  Copyright 2011 by Francesco Pizzitutti
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.app.cystiagents;

import java.util.*;

public class PigAgesComparator implements Comparator, java.io.Serializable
{
   private static final long serialVersionUID = 1L;

   //----------------------------------------------------------
   public PigAgesComparator()
   {
   }

   //----------------------------------------------------------
   public int compare(Object ao1, Object ao2)
   {

      Pig a1 = ((Pig)ao1);
      Pig a2 = ((Pig)ao2);
 
      if(a1.age < a2.age)
          return 1;
      else if(a1.age > a2.age)
          return -1;
      else
          return 0;
   }


}
