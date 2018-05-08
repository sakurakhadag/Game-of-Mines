import java.awt.event.*;
import javax.swing.*;  
import java.util.Random;
import java.awt.font.*;
import java.util.*;
import java.awt.*;

class GridClick implements MouseListener{
    
    //this is all the visible part
    JFrame grid=new JFrame();
    JButton[][] cell=new JButton[9][9];
    
    JButton reset=new JButton("");
   

    JTextField action=new JTextField("");
    Font font1 = new Font("SansSerif", Font.BOLD, 30);
    JTextField flagCount=new JTextField("10");
    
    
    //JTextField cellsClosed=new JTextField("71");//was needed to keep a track of how
                                                //many non bomb cells were left
    int cellsLeft=71;
    
    
    JMenuBar settings=new JMenuBar();
    JMenu menu=new JMenu("Settings");
    JMenuItem level1=new JMenuItem("Level 1");
    JMenuItem level2=new JMenuItem("Level 2");
    JMenuItem level3=new JMenuItem("Level 3");
    
    
    
    int row, col;
    
    GridClick(int x,int y){
    //initialise the GUI
        
        for(row=0; row<9; row++){
            for(col=0; col<9; col++){
                //cell[row][col]=new JButton(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_button.jpg"));
                //new JButton(Icon i) makes a button with icon object
                //then setIcon can be used to set it's icon!
                
                cell[row][col]=new JButton("");               
                //x coord, y coord, length, breadth
                setIcon(cell[row][col],"normal");
                cell[row][col].setBounds(50+row*50,100+col*50,50,50);
                cell[row][col].putClientProperty("col", row);//I will never understand why this is flipped
                cell[row][col].putClientProperty("row",col);
                cell[row][col].putClientProperty("flag",0);//not flagged yet
                cell[row][col].putClientProperty("visible",0);//has not been clicked yet
                cell[row][col].putClientProperty("bomb",0);//has no bomb
                cell[row][col].putClientProperty("bomb",-1);//are are bombs right now
                //I will also have client property "bombCount"

                grid.add(cell[row][col]);
                cell[row][col].addMouseListener(this);
            }
        }
           
        setIcon(reset,"alive");
        //cellsClosed.setBounds(10,50,40,40);
        //action.setBounds(70,50,20,20);
        reset.setBounds((row*50/2)+25,45,50,50);
        flagCount.setBounds((row*50/2)-100,45,50,50);
        flagCount.setFont(font1);
        flagCount.setEditable(false);
        
        //grid.add(cellsClosed);
        grid.add(reset);
        grid.add(flagCount);
        
        reset.addMouseListener(this);
  

        menu.add(level1);
        menu.add(level2);
        menu.add(level3);
        settings.add(menu);
        
        grid.add(settings);
        
        grid.setLayout(null);
        grid.setVisible(true);
        grid.setBounds(x,y,700,700);
        
        //to exit java when figure is closed
        grid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.fillGrid();
        //this.insertBombs();
        
    }
    
    public void Reset(){
    for(row=0; row<9; row++){
            for(col=0; col<9; col++){
                setIcon(cell[row][col],"normal");
                
                cell[row][col].putClientProperty("flag",0);//not flagged yet
                cell[row][col].putClientProperty("visible",0);//has not been clicked yet
                cell[row][col].putClientProperty("bomb",0);//has no bomb
                cell[row][col].putClientProperty("bomb",-1);//are are bombs right now
                cell[row][col].putClientProperty("bombCount",0);

            }
        }
        setIcon(reset,"alive");
        flagCount.setText("10");
        //cellsClosed.setText("71");
        cellsLeft=71;
        this.fillGrid();
    }
    
   
    public void mouseClicked(MouseEvent e){
        
        //status.setText(String.valueOf(e.getClickCount()));
        if(e.getSource()!=reset){
            JButton tempButton=(JButton) e.getSource();
            col=Integer.parseInt(String.valueOf(tempButton.getClientProperty("col")));
            row=Integer.parseInt(String.valueOf(tempButton.getClientProperty("row")));
            int visible=Integer.parseInt(String.valueOf(cell[col][row].getClientProperty("visible")));
            
            if(SwingUtilities.isRightMouseButton(e)){
                if(visible==0)
                    this.RightClick(row,col);
            }
            else if(SwingUtilities.isLeftMouseButton(e)){
                if(String.valueOf(e.getClickCount())=="1" || visible==0){

                    if(visible==0 &&!isFlagged(cell[col][row]))
                        this.LeftClick(row,col);
                }
                else if(visible==1 && !isBomb(cell[col][row])&& Integer.parseInt(String.valueOf(e.getClickCount()))>1){
                    this.DoubleClick(row,col);
                }
            }
        }
        else{
             this.Reset();
        }
     
    }


    private void RightClick(int col,int row){//remember this is switched
           
        int visible=Integer.parseInt(String.valueOf(cell[row][col].getClientProperty("visible")));
        int noOfFlags=Integer.parseInt(flagCount.getText());
        if(visible==0){
            if(!isFlagged(cell[row][col])){
                //set flag
                setIcon(cell[row][col],"flag");
                cell[row][col].putClientProperty("flag",1);
                noOfFlags--;
            }
            else{
                //unset flag
                setIcon(cell[row][col],"normal");
                cell[row][col].putClientProperty("flag",0);
                noOfFlags++;
            }
            flagCount.setText(String.valueOf(noOfFlags));
        }           
    }
    
    private void LeftClick(int col,int row){//remember this is switched
        if(!isBomb(cell[row][col]))
        {//if statement is correct
            if(isEmpty(cell[row][col])){
                int[] params=new int[]{col,row,0};
                params=avalanche(params);
            }
            else{
                int bombCount=Integer.parseInt(String.valueOf(cell[row][col].getClientProperty("bombCount")));
                setIcon(cell[row][col],String.valueOf(bombCount));
                
                cell[row][col].putClientProperty("visible",1);
                cellsLeft--;
            }
            if(cellsLeft==0)
                endGame("win");
                
            //cellsClosed.setText(String.valueOf(cellsLeft));
        }
        else
        {   setIcon(cell[row][col],"bomb_red");
            cell[row][col].putClientProperty("visible",1);
            for(int i=0;i<9;i++){
                for(int j=0;j<9;j++){
                    if(isBomb(cell[i][j]) && !isVisible(cell[i][j])){
                        setIcon(cell[i][j],"bomb");
                        //cell[i][j].putClientProperty("visible",1);//correct
                    }
                    cell[i][j].putClientProperty("visible",1);//make all cells unclickable
                }
            }
            endGame("lose");
        }
    }
    
   
    public void DoubleClick(int col, int row){
        //check if any neighbouring cell is an unflagged bomb, boom!
        
        int count=0, x, y, i=row, j=col;
        int bombCount=Integer.parseInt(String.valueOf(cell[i][j].getClientProperty("bombCount")));
        row=9; col=9;
        int[] tempArray;
        
        for(x=-1;x<2;x++){
            for(y=-1;y<2;y++){
                if(i+x>=0 && j+y>=0 && x+i<row && y+j<col){
                   if(isFlagged(cell[x+i][y+j])){
                        count+=1;//count number of flags
                            
                   }
                }

            }
        }
        
        
        if(count==bombCount){
            //if noOfBombs=noOfFlags, reveal all neighbouring cells
            for(x=-1;x<2;x++){
                for(y=-1;y<2;y++){
                    if(x>0)
                        System.out.print("Pos\n");
                    if(x+i>=0 && y+j>=0 && x+i<row && y+j<col){
                        if(!isFlagged(cell[x+i][y+j]) && !isBomb(cell[x+i][y+j])){//if not flagged and is not a bomb, reveal

                            if(!isVisible(cell[x+i][y+j])){
                                cell[x+i][y+j].putClientProperty("visible",1);
                                cellsLeft--;
                                System.out.print(x+i+" "+ y+j +" \n");
                            }
                            else
                                System.out.print("Visible"+x+"+"+i+" "+y+"+"+j +" \n");
                                
                            
                            bombCount=Integer.parseInt(String.valueOf(cell[x+i][y+j].getClientProperty("bombCount")));
                            setIcon(cell[x+i][y+j],String.valueOf(bombCount));
                           
                            if(isEmpty(cell[x+i][y+j])){
                                tempArray=new int[]{y+j,x+i,0};
                                tempArray=avalanche(tempArray);
                                //x=tempArray[1]; what did I have this for?
                                //y=tempArray[0];
                            }
                        }
                        else if(!isBomb(cell[x+i][y+j]) && isFlagged(cell[x+i][y+j])){//if the wrong cell is flagged
                            //game lost
                            //the flagged cell explodes
                            setIcon(cell[x+i][y+j],"bomb_red");
                            cell[x+i][y+j].putClientProperty("visible",1);
                            
                            //if any other flag is not bomb, show it
                            showWrongFlags(j,i);
                            //explode all other bombs
                            for(i=0;i<row;i++){
                                for(j=0;j<col;j++){
                                    if(isBomb(cell[i][j]) && !isVisible(cell[i][j])){
                                        setIcon(cell[i][j],"bomb");
                                        cell[i][j].putClientProperty("visible",1);//correct
                                    }
                                }
                            }
                                                                   
                            endGame("lose");
                                                  
                        }
                    }
                }
            }
        }
        
        if(cellsLeft==0)
            endGame("win");
                
        //cellsClosed.setText(String.valueOf(cellsLeft));
    
    }
    
    private void showWrongFlags(int col,int row){
    int i=row, j=col;
    int x,y;
    row=9;col=9;
    //in case a cell is flagged wrong, identify
    
    for(x=-1;x<2;x++){
            for(y=-1;y<2;y++){
                if(i+x>=0 && j+y>=0 && x+i<row && y+j<col){
                        if(!isBomb(cell[x+i][y+j]) && isFlagged(cell[x+i][y+j])){//if flagged but not a bomb
                            setIcon(cell[x+i][y+j],"minewrong");
                            cell[x+i][y+j].putClientProperty("visible",1);
                            cellsLeft--;
                        }
                    }
                }
    }
    //cellsClosed.setText(String.valueOf(cellsLeft));
   }
    
   public void fillGrid(){
       this.insertBombs();
       int count=0,x,y;
       row=9; col=9;
            
       for(int i=0;i<9;i++){
           for(int j=0;j<9;j++){
               
               if(!isBomb(cell[i][j])){
                   count=0;
                   for(x=-1;x<2;x++){
                      for(y=-1;y<2;y++){
                           if(i+x>=0 && j+y>=0 && i+x<row && j+y<col && isBomb(cell[x+i][y+j])){
                               count+=1;
                           }
                      }
                   }
                   cell[i][j].putClientProperty("bombCount",count);
               }
               
           }
       }
   
   }
    
   public void insertBombs(){
       row=9;
       col=9;
       int noOfBombs=10;
       int count=0,x,y;
       Random rand=new Random();
       System.out.println(count);
       while(count!=noOfBombs){
           x=rand.nextInt(row);
           y=rand.nextInt(col);
           
           if(!isBomb(cell[y][x])){
               cell[y][x].putClientProperty("bomb",1);
               count+=1;
           }
       }
   }
   
   private int[] avalanche(int params[])
   {
      int x,i=params[1],j=params[0],count=params[2];
      int y=-1;
      int[] tempArray;
      int bombCount=Integer.parseInt(String.valueOf(cell[i][j].getClientProperty("bombCount")));
      row=9; col=9;
      for(x=-1;x<2;x++){
          for(y=-1;y<2;y++){
              if(i+x>=0 && j+y>=0 && i+x<row && j+y<col){
                  if(!isBomb(cell[x+i][y+j]) && !isVisible(cell[x+i][y+j])){
                      cell[x+i][y+j].putClientProperty("visible",1);
                      count+=1;
                      cellsLeft--;
                      bombCount=Integer.parseInt(String.valueOf(cell[x+i][y+j].getClientProperty("bombCount")));
                      setIcon(cell[x+i][y+j],String.valueOf(bombCount));
                      
                      if(isEmpty(cell[x+i][y+j])){
                          tempArray=new int[]{y+j,x+i,count};
                          tempArray=avalanche(tempArray);
                          tempArray[1]-=x;
                          tempArray[0]-=y;
                          i=tempArray[1];
                          j=tempArray[0];
                      }
                  }
              }
          }
      }
      if(!isVisible(cell[i][j])){
          cell[i][j].putClientProperty("visible",1);
          bombCount=Integer.parseInt(String.valueOf(cell[i][j].getClientProperty("bombCount")));
          setIcon(cell[i][j],String.valueOf(bombCount));
          count+=1;
          cellsLeft--;
      }
      params[0]=j;
      params[1]=i;
      params[2]=count;
      return params;
  }
  
   public void endGame(String result)
   {
       String path;
       row=9;col=9;
       
       if(result=="lose"){
            path="C:\\Users\\Ishita\\Desktop\\Python\\minesweeper\\minesweeper_dead.jpg";
       }
       else{
           path="C:\\Users\\Ishita\\Desktop\\Python\\minesweeper\\minesweeper_win.jpg";        
           //also, if bombs revealed!=10, reveal all bombs
           int noOfFlags=Integer.parseInt(flagCount.getText());
           if(noOfFlags!=0){
               for(int i=0;i<row;i++){
                   for(int j=0;j<col;j++){
                       if(isBomb(cell[i][j]) && !isVisible(cell[i][j])){
                           setIcon(cell[i][j],"flag");
                           cell[i][j].putClientProperty("visible",1);
                           cell[i][j].putClientProperty("flag",1);
                           noOfFlags--;
                       }
                   }
               }
               flagCount.setText(String.valueOf(0));
           }
       }
       
       ImageIcon icon = new ImageIcon(path);
       Image img = icon.getImage();  
       Image newImg = img.getScaledInstance( 50,50,java.awt.Image.SCALE_SMOOTH ) ;
       reset.setIcon(new ImageIcon(newImg));
       
       img=null;
       newImg=null;
       
       
      
   }
   
   private void setIcon(javax.swing.JButton cell,String name){
       String path="C:\\Users\\Ishita\\Desktop\\Python\\minesweeper\\minesweeper_"+name+".jpg";
       ImageIcon icon = new ImageIcon(path);
       Image img = icon.getImage();  
       Image newImg = img.getScaledInstance( 50,50,java.awt.Image.SCALE_SMOOTH ) ;
       cell.setIcon(new ImageIcon(newImg));
       img=null;
       newImg=null;
   }
    
   private boolean isBomb(javax.swing.JButton cell)
   {
       
       if(Integer.parseInt(String.valueOf(cell.getClientProperty("bomb")))==1)
           return true;
       else
           return false;
   }
   
   private boolean isFlagged(javax.swing.JButton cell)
   {
        
       if(Integer.parseInt(String.valueOf(cell.getClientProperty("flag")))==1)
           return true;
       else
           return false;
   }    
     private boolean isEmpty(javax.swing.JButton cell)
   {
       
       if(Integer.parseInt(String.valueOf(cell.getClientProperty("bombCount")))==0)
           return true;
       else
           return false;
   }    
   
   private boolean isVisible(javax.swing.JButton cell)
   {
       
       if(Integer.parseInt(String.valueOf(cell.getClientProperty("visible")))==1)
           return true;
       else
           return false;
   }
   
   private int[] getPos()
   {
       //makes sure that the new window pops where the old one was
       //finds the position and passes it on
       String pos=grid.getLocationOnScreen().toString();
       
       int x=0,y=0;
       int[] coords=new int[2];
       
       for(int i=0;i<pos.length();i++){
           if(pos.charAt(i)=='x'){
               i=i+2;
               x=0;
               while(pos.charAt(i)!=','){
                   x=x*10+((int) pos.charAt(i))-48;
                   i++;
               }
               i++;
            
               if(pos.charAt(i)=='y'){
                   i=i+2;
                   y=0;
                   while(pos.charAt(i)!=']'){
                       y=y*10+((int) pos.charAt(i))-48;
                       i++;
                   }    
                   break;
              }
           }
  
       }
       coords[0]=x;coords[1]=y;
       //System.out.println(x+"+"+y);
       return coords;
   }
   
   public void mouseExited(MouseEvent e){}
   public void mouseEntered(MouseEvent e){}
   public void mouseReleased(MouseEvent e){}
   public void mousePressed(MouseEvent e){}
   
   public static void main()
   {
       GridClick g1=new GridClick(0,0);
       
   }
}
