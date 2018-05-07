//import java.awt.*;
import java.awt.event.*;
import javax.swing.*;  
import java.util.Random;
import java.util.*;

class MinesweeperGrid implements MouseListener, ActionListener{
    
    //this is all the visible part
    JFrame grid=new JFrame();
    JButton[][] cell=new JButton[9][9];
    JTextField status=new JTextField(""); 
    JMenuBar settings=new JMenuBar();
    JMenu menu=new JMenu("Settings");
    JMenuItem level1=new JMenuItem("Level 1");
    JMenuItem level2=new JMenuItem("Level 2");
    JMenuItem level3=new JMenuItem("Level 3");
    

    
    // for small minesweeper, grid is 9*9 (no of bombs=10)
    //for larger ones, grid is 16*16 (no of bombs=40)
    public int row,col,level=0,noOfBombs,bombCount;
    public int[] gridDimensions;
    public int[][] display_grid;
    public int[][] back_grid;
    
    
    MinesweeperGrid(int level){
        
        //initialise the grids
        
        level=0;
        
        if(level==0){
            display_grid=new int[9][9];
            back_grid=new int[9][9];
            noOfBombs=10;
            gridDimensions=new int[]{9,9};
           
        }
        else if(level==1){
            display_grid=new int[16][16];
            back_grid=new int[16][16];
            noOfBombs=40;
            gridDimensions=new int[]{16,16};
        }
        else{
            display_grid=new int[16][30];
            back_grid=new int[16][30];
            noOfBombs=99;
            gridDimensions=new int[]{16,30};
        }
        
        bombCount=0;
        //fill both grids
        this.fillGrid();
        
        //initialise the GUI
        for(row=0; row<9; row++){
            for(col=0; col<9; col++){
                cell[col][row]=new JButton(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_button.jpg"));
                //new JButton(Icon i) makes a button with icon object
                //then setIcon can be used to set it's icon!
                
                               
                //x coord, y coord, length, breadth
                cell[col][row].setBounds(50+row*50,50+col*50,50,50);
                cell[col][row].putClientProperty("col", col);
                cell[col][row].putClientProperty("row",row);
                cell[col][row].putClientProperty("flag",0);

                grid.add(cell[col][row]);
                cell[col][row].addMouseListener(this);
            }
        }
                      
        status.setBounds(580, 100, 100,50);
        grid.add(status);
          
        menu.add(level1);
        menu.add(level2);
        menu.add(level3);
        level1.addActionListener(this);
        level2.addActionListener(this);
        level3.addActionListener(this);
        
        settings.add(menu);
        
        grid.add(settings);
        
        grid.setLayout(null);
        grid.setVisible(true);
        grid.setSize(700,700);
        
        //to exit java when figure is closed
        grid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
   
    public void mouseClicked(MouseEvent e){
        
        //status.setText(String.valueOf(e.getClickCount()));
        // 1 left click
        // 3 is right click
        JButton tempButton=(JButton) e.getSource();
        col=Integer.parseInt(String.valueOf(tempButton.getClientProperty("row")));
        row=Integer.parseInt(String.valueOf(tempButton.getClientProperty("col")));
            
        if(SwingUtilities.isRightMouseButton(e)){
            int flag=Integer.parseInt(String.valueOf(tempButton.getClientProperty("flag")));
            
            if(flag==0){
                bombCount+=1;
                cell[row][col].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_flag.jpg"));
            }else{
                bombCount-=1;
                cell[row][col].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_button.jpg"));
            }
            flag=(flag+1)%2;
            cell[col][row].putClientProperty("flag",flag);
            display_grid[col][row]=0+flag*2;
            
            status.setText(String.valueOf(bombCount));
            if(bombCount==noOfBombs)
                this.check();
        }
        else if(SwingUtilities.isLeftMouseButton(e)){
            if(e.getClickCount()==1){
                System.out.print(row+"+"+col+"\\");
                if(back_grid[col][row]!=-1 && display_grid[col][row]==0){
                    //cell[row][col].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_1.jpg"));
                    display_grid[col][row]=1;
                    this.changeIcon(row,col,back_grid[col][row]);
                    if(back_grid[col][row]==0){
                        int[] params=new int[]{col,row,0};
                        this.avalanche(params);
                    
                    }
                }else if(display_grid[col][row]==0){//if unopened bomb=>explode
                    this.terminateGame();
                    //cell[col][row].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_bomb.jpg"));
                }else{
                    //tried to click an invalid cell
                }
                //ew ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_1.jpg"));
            }
            else if(display_grid[col][row]==1){
                //reveal empty cells
                int[] params=new int[]{row,col,1};
                this.revealCells(params);
                //cell[col][row].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_bomb.jpg"));
            }
            
        }
        
        
    }
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
   
    public void actionPerformed(ActionEvent e){
        if(e.getSource()=="level1")
            level=0;
        else if(e.getSource()=="level2")
            level=1;
        else
            level=2;
        
        grid.setVisible(false);
        new MinesweeperGrid(level);
    }
    
    public MinesweeperGrid fillGrid(){
        this.insertBombs();
        
        col=gridDimensions[0];
        row=gridDimensions[1];
        
        int i,j,count;
        
        for(i=0;i<row;i++)
        {
            for(j=0;j<col;j++){
                if(back_grid[j][i]!=-1){
                    count=0;
                    for(int x=-1;x<2;x++){
                        for(int y=-1;y<2;y++){
                            if(i+x>=0 && j+y>=0 && i+x<row && j+y<col && back_grid[y+j][x+i]==-1){
                                count+=1;
                            }
                        }
                    }
                    back_grid[j][i]=count;
                }
                display_grid[j][i]=0;
            }
        }
        this.display();
        return this;
    }
    
    public MinesweeperGrid insertBombs(){
        col=gridDimensions[0];
        row=gridDimensions[1];
        int count=0,x,y;
        Random rand=new Random();
        System.out.println(noOfBombs);
        while(count!=noOfBombs){
            x=rand.nextInt(row);
            y=rand.nextInt(col);
            
            if(back_grid[y][x]!=-1){
                back_grid[y][x]=-1;
                count+=1;
            }
        }
        return this;
    }
        
    public int[] avalanche(int[] params){
        int i=params[1],j=params[0],count=params[2];
        int x,y;
        
        int[] tempArray;
        
        col=gridDimensions[0];
        row=gridDimensions[1];
        
        for(x=-1;x<2;x++){
            for(y=-1;y<2;y++){
                if(i+x>=0 && j+y>=0 && i+x<row && j+y<col){
                    if(x!=0 && y!=0 && back_grid[y+j][x+i]!=-1 && display_grid[y+j][x+i]==0){
                        display_grid[x+i][y+j]=1;
                        //cell[x+i][y+j].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_1.jpg"));
                        this.changeIcon(x+i,y+j,back_grid[x+i][y+j]);    
                        count+=1;
                        if(back_grid[x+i][y+j]==0){
                            tempArray=new int[]{y+j,x+i,count};
                            tempArray=avalanche(tempArray);
                            i=tempArray[1]-x;
                            j=tempArray[0]-y;
                        }
                    }
                }
            }
        }
        try{
            display_grid[i][j]=1;
            this.changeIcon(i,j,back_grid[i][j]);
            //cell[i][j].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_1.jpg"));
            count+=1;
        }catch(ArrayIndexOutOfBoundsException er){
            System.out.println("j,i="+j+i);
        }
        
        params[2]=count;
        return params;
    }
    
    public MinesweeperGrid changeIcon(int col,int row,int number){
        String s="C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_";
        s=s+String.valueOf(number)+".jpg";
        cell[col][row].setIcon(new ImageIcon(s));
        return this;
        
    }
    
    public void display(){
        System.out.print('\u000C'); //clears screen
        System.out.print("\n\t\t\t   |");
        int i,j;
        col=gridDimensions[0]; 
        row=gridDimensions[1];
        
        for(j=0;j<col;j++){
            if(j<10)
                System.out.print("0"+String.valueOf(j)+" ");
            else
                System.out.print(String.valueOf(j)+" "); 
        }
        
        System.out.print("\n\t\t\t");
        for(j=0;j<col*4+3;j++){
            System.out.print("_");
        }
        
        System.out.println();
        
        for(i=0;i<row;i++){
            if(i<10)
                System.out.print("\t\t\t0"+String.valueOf(i)+" |");
            else
                System.out.print("\t\t\t"+String.valueOf(i)+" |");
            for(j=0;j<col;j++){
                //if(display_grid[j][i]==1){
                    if(back_grid[i][j]==-1){
                    //    System.out.print(" "+back_grid[j][i]);
                    //}
                    //else{
                        System.out.print("|B|");
                    }
                    else{
                        System.out.print(" "+String.valueOf(back_grid[i][j])+" ");
                    }
                    //}
                //}
                //else if(display_grid[j][i]==2){
                //    System.out.print("  X");
                //}
                //else{
                //    System.out.print("  -");
                //}
            }
            System.out.println();
        }
    }
     
    public void revealCells(int[] params){
        int i=params[1],j=params[0],f=params[2];
        int count=0,x,y;
        col=gridDimensions[0];
        row=gridDimensions[1];
        
        for(x=-1;x<2;x++){
            for(y=-1;y<2;y++){
                if(i+x>=0 && j+y>=0 && x+i<row && y+j<col){
                    if(display_grid[x+i][y+j]==2){//if flagged
                        if(back_grid[x+i][y+j]!=-1){//but not a bomb
                            System.out.println("\t\tYou flagged the wrong cell");
                            //insert pop up
                            terminateGame();
                        }
                        else{
                            count+=1;
                        }
                    }
                }
            }
        }
        
        if(count==back_grid[i][j]){
            //if all bombs are flagged, reveal all neighbouring cells
            for(x=-1;x<2;x++){
                for(y=-1;y<2;y++){
                    if(x+i>=0 && y+j>=0 && x+i<row && y+j<col){
                        if(display_grid[x+i][y+j]!=2){//if not flagged
                            display_grid[x+i][y+j]=1;//display
                            this.changeIcon(x+i,y+j,back_grid[x+i][y+j]);
                           
                            if(back_grid[x+i][y+j]==0){
                                int[] tempArray=new int[]{y+j,x+i,0};
                                tempArray=avalanche(tempArray);
                                x=tempArray[1];
                                y=tempArray[0];
                                count=tempArray[2];
                            }
                        }
                    }
                }
            }
        }
        else{
            //if(f==0){
            //    System.out.println("\t\t\tCannot change a revealed cell");
            System.out.println("Why not?");
        
            
        }
    }
                                
    public boolean check(){
        //checks for winning
        col=gridDimensions[0];
        row=gridDimensions[1];
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                if(back_grid[i][j]!=-1 && display_grid[i][j]!=1){
                    //if all non Bomb cells are not revealed
                    //no win
                    return false;
                }
            }
        }
        
        this.display();
        
        System.out.println("\n\t\t\tYou Win!");
        //trigger pop up?
        //exit?
        System.exit(0);
        return true;
    }
    
    public void terminateGame(){
        for(int i=0;i<gridDimensions[1];i++){
            for(int j=0;j<gridDimensions[0];j++){
                if(back_grid[j][i]==-1){
                    display_grid[j][i]=1;
                    cell[j][i].setIcon(new ImageIcon("C:\\Users\\Ishita\\Desktop\\Python\\minesweeper_bomb.jpg"));
                }
            }
        }

        this.display();
        System.out.println("\n\t\t\tYou hit a bomb!");
        
        //add delay, don't clolse. Disable all?
        //pop up?
        
        //System.exit(0);
    }
    
    
public static void main(String[] args)
{
    new MinesweeperGrid(0);
    
}
}