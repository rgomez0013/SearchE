package test;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.*;

public class SearchEngine 
{
    private HashMap<Integer, ArrayList<Entry>> indexHM = new HashMap<Integer, ArrayList<Entry>>(); 
    private ArrayList<ArrayList<String>> plays = new ArrayList<ArrayList<String>>();
    private int playCount = 0;
 
    //create a hashKey to index the index
    int hashKey(String key)
    {
        return key.hashCode();
    }
	

    
    private String getLineWithoutPunct(String line)
    {
        String newLine = "";
        newLine = line.replace('\t', ' ');
        newLine = newLine.replaceAll("[\\p{P}&&[^\u0027]]", " ");
        
        return newLine;
    }
    
    
    private void processLine(String line, String title, String act, String scene, String character)
    {
        String originalLine = line;
        //remove punctuation except apostrophe	
        line = line.trim();
        line = line.replace('\n', ' ');
        line = this.getLineWithoutPunct(line);
        
        //split by space
        String[] val1 = line.trim().split("\\s");

        for (int i = 0; i < val1.length; i++)
        {
            String word = val1[i];
            
            //only catalog words 4 characters or longer
            if (word.length() < 4)
                continue;
            
            //create a new entry for this word
            Entry w = new Entry(title, act, scene, character, word, originalLine);

            //get hashkey for this word
            int indexVal = hashKey(word);
            ArrayList<Entry> al = indexHM.get(indexVal);
            if (al == null)
            {
                    al = new ArrayList<Entry>();
                    al.add(w);
                    indexHM.put(indexVal,al);
            }
            else
            {
                    al.add(w);
            }
        }

    }


    public void buildIndex() 
    {
        String fileName = "";
        String fileSep = System.getProperty("file.separator");
        

        //find the list of files
        File folder = new File(System.getProperty("home/codio/workspace/src/main/java/cms330") + fileSep + "texts");        
        
        for (final File fileEntry : folder.listFiles()) 
        {
          if (fileEntry.isDirectory()) 
          {
            // skip it, we are only handling .txt files in the texts directory
          } 
          else 
          {
            if (fileEntry.isFile()) 
            {
              String temp = fileEntry.getName();
              if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("txt"))
                fileName = folder.getAbsolutePath()+ fileSep + fileEntry.getName();
            }

            processFile(fileName, playCount);
            playCount++;
            
           }//end if (fileEntry.isDirectory()) 
        } //end for (final File fileEntry : folder.listFiles()) 

    }

private void processCharacter(Scanner scan, StringBuilder sbLine, String title, String act, String scene, String character)
{
    //gotta process the LINE we came in on, and then continue until the line does NOT start with a tab

    do
    {
        if (sbLine.length() == 0)
            break;
        
        processLine(sbLine.toString(), title, act, scene, character);
        sbLine.setLength(0); //clear the line for next one

        if (scan.hasNextLine())
            sbLine.append(scan.nextLine());
        else
            break;

        if (sbLine.toString().startsWith("\t["))
           this.processStageDirections(scan, sbLine, title, act, scene);
        
        if (!sbLine.toString().startsWith("\t"))
           break;

    }while(scan.hasNextLine());
}    
 
    private void processStageDirections(Scanner scan, StringBuilder sbLine, String title, String act, String scene)
    {
        String character = "";
        boolean done = false;
        //we are ON the first line of the stage direction
        
        do
        {            
            if (!sbLine.toString().startsWith("\t"))
                 done = true;
            else
            {
                processLine(sbLine.toString(), title, act, scene, character);  
                sbLine.setLength(0);

                if (scan.hasNextLine())
                    //get a line of text
                    sbLine.append(scan.nextLine());            
            }
        }while (!done);

    }
    
    private void processPROLOGUE(Scanner scan, StringBuilder sbLine, String title)
    {
        String act = "PROLOGUE";
        String scene = "";
        String character = "";
                
        scan.nextLine(); //skip blank line
        
        //loop through and store the lines of the prologue
        
        while (scan.hasNextLine())
        {
            //get a line of text
            sbLine.append(scan.nextLine());

            if (sbLine.toString().trim().length() == 0)
                break; //the prologue is over get out
            
            if (!sbLine.toString().startsWith("\t"))
                 break;
            
            processLine(sbLine.toString(), title, act, scene, character);  
            sbLine.setLength(0); //clear the string builder
        }
    }
       
    
    
        
     private void processFile(String fileName, int playNum)
     {
        File file1 = new File(fileName);
        Scanner scan =  null;
        
        try 
        {
            // scan file
            scan = new Scanner(file1);
            String title = ""; 

            //first line is the title
            if (scan.hasNextLine())
            {
                String nl = scan.nextLine();
                nl = nl.trim();
                
                title = nl;                
            }
            String act = "";
            String scene = "";
            String character = "";
            String firstWord = "";
            StringBuilder sbLine = new StringBuilder(); //use string builder object so it can be changed by other methods
            
            while (scan.hasNextLine()) 
            {
                //we may have already read the line in, if not - get a new one
                if (sbLine.toString().length() == 0) 
                    sbLine.append(scan.nextLine());

                //if the line is blank, skip it
                if (sbLine.toString().length() == 0)
                    continue; //start while over
                
                if (sbLine.toString().startsWith("\t["))
                    this.processStageDirections(scan, sbLine, title, act, scene);

                //if the line is blank, skip it
                if (sbLine.toString().length() == 0)
                    continue; //start while over
                
                //check 1st word for location (all caps) either PROLOGUE, ACT or SCENE
                //get the 1st word of the line
                firstWord = "";
                
                String nl = sbLine.toString().replace('\t', ' ');
                int i = nl.indexOf(' ');
                if (i < 0)
                {
                    firstWord = nl;
                }
                else
                {
                    firstWord = nl.substring(0, i);
                }
        
                if (firstWord.equals("PROLOGUE"))
                {
                    processPROLOGUE(scan, sbLine, title);
                }

                if (sbLine.toString().trim().length() == 0)
                    continue; // start while over again
                
                if (firstWord.equals("ACT")) //act changes 
                {

                    //replace tabs with spaces
                    nl = sbLine.toString().replace('\t', ' ');
                    nl = nl.trim();
                    nl = nl.substring(4);
                    //what if the act is the last thing on the line
                    i = nl.indexOf(' ');
                    if (i < 0)
                    {
                        firstWord = nl;
                    }
                    else
                    {
                        firstWord = nl.substring(0, i);
                    }
                    act = firstWord;
                    scene = "";
                    character = "";
                    
                    sbLine.setLength(0);  //ignore the rest of the line
                    continue; //go to the next line
                }
                    
                if (sbLine.toString().trim().length() == 0)
                    continue; //go back to start of while

                if (firstWord.equals("SCENE")) //scene changes 
                {
                    //replace tabs with spaces
                    nl = sbLine.toString().replace('\t', ' ');
                    nl = nl.trim();
                    nl = nl.substring(6);

                    i = nl.indexOf(' ');
                    if (i < 0)
                    {
                        firstWord = nl;
                    }
                    else
                    {
                        firstWord = nl.substring(0, i);
                    }

                    scene = firstWord;
                    character = "";
                    //scene should be on its own line - we are ignoring location info
                    sbLine.setLength(0);
                    continue; //ignore the rest of the line
                }   //(firstWord.equals("SCENE")) //scene changes 

                if (sbLine.toString().trim().length() == 0)
                    continue; //go back to start of while


                ////CHARACTER
                //if we got to here, we should be on a new character line

                i = sbLine.toString().indexOf('\t');
                
                //then we need to get the new character name
                character = sbLine.toString().substring(0, i);
                sbLine.delete(0, i);

                this.processCharacter(scan, sbLine, title, act, scene, character);
                   
            }
        } 
        catch (Exception ex) 
        {
            System.out.print(ex);
        }
        finally
        {
            if (scan != null)
                scan.close();
        }
    }


    public SearchEngine() {
    }

    private void printList(ArrayList<Entry> listToPrint)
    {
      try{  
      //print the entire hashMap of entries
        for (Entry e: listToPrint)
        {

            System.out.print(e.toString());
            System.out.println();
            System.out.println();
            System.out.println("-------------------------------");
            System.out.println();

        }
        
      } catch (Exception e){
        System.out.print(" The new word was not found.");
      }
    }

  
    private String getPrintList(ArrayList<Entry> listToPrint)
    {
      StringBuilder sb = new StringBuilder();
      
      try{  
      //print the entire hashMap of entries
        for (Entry e: listToPrint)
        {

            sb.append(e.toString());
            sb.append("\n\n");
            sb.append("-------------------------------\n");
          
        }
        
      } catch (Exception e){
        sb.append(" The word was not found.");
      }
      return sb.toString();
    }

  
  
  
    public void printIndex()
    {
        //print the entire hashMap of entries
        for (Integer i: indexHM.keySet())
        {
                ArrayList<Entry> a = indexHM.get(i);

                //loop through the arraylist and print entries
                for (Entry e: a)
                {
                        System.out.println("WORD: " + e.getWord());
                        System.out.print(e.toString());
                        System.out.println();
                        System.out.println();
                }

        }
    }

    public ArrayList<Entry> getListOfMatchesDisregardCase(String wordToMatch)
    {
        //so this will return the arraylist at this key value
        int indexVal = this.hashKey(wordToMatch);
        
        ArrayList<Entry> bigList = this.indexHM.get(indexVal);
        
        return bigList;
        
    }
    
    public ArrayList<Entry> getListOfMatchesCaseMatters(String wordToMatch)
    {
        ArrayList<Entry> bigList = this.getListOfMatchesDisregardCase(wordToMatch);

        //now build a new arraylist with ONLY those that match the case exactly
        ArrayList<Entry> newList = new ArrayList<>();
        
        for (Entry e: bigList)
        {
            if (e.getWord().equals(wordToMatch))
            {              
               newList.add(e);
                
            }
        }
        
        return newList;
    }
    
    public void printListOfMatchingCases(String wordToMatch, boolean matchCase)
    {
        ArrayList<Entry> listToPrint = null;

        if (matchCase)
            listToPrint = this.getListOfMatchesCaseMatters(wordToMatch);
        else
            listToPrint = this.getListOfMatchesDisregardCase(wordToMatch);
        
        printList(listToPrint);
    }

    public String getPrintListOfMatchingCases(String wordToMatch, boolean matchCase)
    {
        ArrayList<Entry> listToPrint = null;

        if (matchCase)
            listToPrint = this.getListOfMatchesCaseMatters(wordToMatch);
        else
            listToPrint = this.getListOfMatchesDisregardCase(wordToMatch);
        
        return getPrintList(listToPrint);
    }
  
}