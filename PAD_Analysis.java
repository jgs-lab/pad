package pad_analysis;

import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PAD_Analysis
{
    
    public static void main(String[] args)
    {
        String fileName = "TestRun.csv";
        CSVReader reader = null;
        List<String[]> allRows;
        int currentVid = 0;
        Double[] averagePDA = new Double[] {0d,0d,0d};
        Double[] stdPDA = new Double[] {0d,0d,0d};
        Double[][] videoPDA = new Double[8][3];
        Double[][] vidPDA_std = new Double[8][3];
        Double vidStartTime = 0d;
        Double vidLength = 60000d;
        int dataSum = 0;
        Double[][] expPDA = new Double[8][3];
        Double sqrTemp = 0d;
        
        try
        {
            reader = new CSVReader(new FileReader(fileName));
            
            allRows = reader.readAll();
            
            ////FIND AVG
            vidStartTime = Double.parseDouble(allRows.get(0)[1]);
            //set current video length
            
            for(String[] row : allRows)
            {
                if(Integer.parseInt(row[0].replaceAll("[\\D]", "")) == currentVid)
                {
                    if((Double.parseDouble(row[1]) - vidStartTime) <= vidLength)
                    {
                        dataSum++;
                        averagePDA[0] = averagePDA[0] + Double.parseDouble(row[2]);
                        averagePDA[1] = averagePDA[1] + Double.parseDouble(row[3]);
                        averagePDA[2] = averagePDA[2] + Double.parseDouble(row[4]);
                    }
                }
                else
                {
                    averagePDA[0] = averagePDA[0] / dataSum;
                    averagePDA[1] = averagePDA[1] / dataSum;
                    averagePDA[2] = averagePDA[2] / dataSum;
                    
                    videoPDA[currentVid] = averagePDA;
                    
                    dataSum = 0;
                    averagePDA = new Double[] {0d,0d,0d};
                    
                    dataSum++;
                    averagePDA[0] = averagePDA[0] + Double.parseDouble(row[2]);
                    averagePDA[1] = averagePDA[1] + Double.parseDouble(row[3]);
                    averagePDA[2] = averagePDA[2] + Double.parseDouble(row[4]);
                    
                    currentVid++;
                    vidStartTime = Double.parseDouble(row[1]);
                    //set current video length
                }
            }
            
            averagePDA[0] = averagePDA[0] / dataSum;
            averagePDA[1] = averagePDA[1] / dataSum;
            averagePDA[2] = averagePDA[2] / dataSum;
            
            videoPDA[currentVid] = averagePDA;
            
            dataSum = 0;
            averagePDA = new Double[] {0d,0d,0d};
            currentVid = 0;
            vidStartTime = 0d;
            //set current video length
            
            ////End of average computation
            
            //test printing averages 0-4 (5 videos)
            System.out.println(Arrays.toString(videoPDA[0]));
            System.out.println(Arrays.toString(videoPDA[1]));
            System.out.println(Arrays.toString(videoPDA[2]));
            System.out.println(Arrays.toString(videoPDA[3]));
            System.out.println(Arrays.toString(videoPDA[4]));
            System.out.println();
            
            ////FIND STDEV
            vidStartTime = Double.parseDouble(allRows.get(0)[1]);
            //set current video length
            
            for(String[] row : allRows)
            {
                if(Integer.parseInt(row[0].replaceAll("[\\D]", "")) == currentVid)
                {
                    if((Double.parseDouble(row[1]) - vidStartTime) <= vidLength)
                    {
                        dataSum++;
                        sqrTemp = (Double.parseDouble(row[2]) - videoPDA[currentVid][0]);
                        stdPDA[0] = stdPDA[0] + (sqrTemp * sqrTemp);
                        sqrTemp = (Double.parseDouble(row[3]) - videoPDA[currentVid][1]);
                        stdPDA[1] = stdPDA[1] + (sqrTemp * sqrTemp);
                        sqrTemp = (Double.parseDouble(row[4]) - videoPDA[currentVid][2]);
                        stdPDA[2] = stdPDA[2] + (sqrTemp * sqrTemp);
                    }
                }
                else
                {
                    stdPDA[0] = Math.sqrt(stdPDA[0] / dataSum);
                    stdPDA[1] = Math.sqrt(stdPDA[1] / dataSum);
                    stdPDA[2] = Math.sqrt(stdPDA[2] / dataSum);
                    
                    vidPDA_std[currentVid] = stdPDA;
                    
                    dataSum = 0;
                    stdPDA = new Double[] {0d,0d,0d};
                    
                    dataSum++;
                    sqrTemp = (Double.parseDouble(row[2]) - videoPDA[currentVid][0]);
                    stdPDA[0] = stdPDA[0] + (sqrTemp * sqrTemp);
                    sqrTemp = (Double.parseDouble(row[3]) - videoPDA[currentVid][1]);
                    stdPDA[1] = stdPDA[1] + (sqrTemp * sqrTemp);
                    sqrTemp = (Double.parseDouble(row[4]) - videoPDA[currentVid][2]);
                    stdPDA[2] = stdPDA[2] + (sqrTemp * sqrTemp);
                    
                    currentVid++;
                    vidStartTime = Double.parseDouble(row[1]);
                    //set current video length
                }
            }
            
            stdPDA[0] = Math.sqrt(stdPDA[0] / dataSum);
            stdPDA[1] = Math.sqrt(stdPDA[1] / dataSum);
            stdPDA[2] = Math.sqrt(stdPDA[2] / dataSum);
            
            vidPDA_std[currentVid] = stdPDA;
            
            dataSum = 0;
            stdPDA = new Double[] {0d,0d,0d};
            currentVid = 0;
            vidStartTime = 0d;
            //set current video length
            
            ////End of std dev computation
            
            //test printing std devs 0-4 (5 videos)
            System.out.println(Arrays.toString(vidPDA_std[0]));
            System.out.println(Arrays.toString(vidPDA_std[1]));
            System.out.println(Arrays.toString(vidPDA_std[2]));
            System.out.println(Arrays.toString(vidPDA_std[3]));
            System.out.println(Arrays.toString(vidPDA_std[4]));
            System.out.println();
            
            ////FIND SIMILARITY OF AVG
            ////End of average similarity computation
            
            ////FIND SIMILARITY OF STDEV
            ////End of std dev similarity computation
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("DONE");
        
    }
    
}
