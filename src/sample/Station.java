package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

import static sample.Controller.INTERVAL;

public class Station {

    public enum Direction {north, south, east, west};

    Direction direction;

    int locX, locY, color;
    static int counter=0;
    int id;
    static int length = 2;
    static int numColors =8;




    public Station(int x, int y, int color, Direction direction){
        locX = x; locY = y; this.color = color; this.direction = direction;
        id = counter ++;
    }

    public static void setLength(int length){
        Station.length = length;
    }

    public static void setColors(int colors){
        Station.numColors = colors;
    }

    public void setPosition(int x, int y){
        locX = x; locY = y;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void draw(GraphicsContext gc, double time){
        //start by drawing the stations
        gc.setFill(Paint.valueOf("red"));
        gc.fillOval(locX*Main.GRIDX +Main.BUFFERX, locY*Main.GRIDY+Main.BUFFERY, Main.GRIDX-Main.BUFFERX, Main.GRIDY-Main.BUFFERY);
        gc.setFill(Paint.valueOf("black"));
        gc.fillText(Integer.toString(color),locX*Main.GRIDX+ Main.GRIDX/3, locY*Main.GRIDY + 3*Main.GRIDY/4);

        //now draw trains as appropriate
        gc.setFill(Paint.valueOf("green"));
        switch(direction){
            case east:
            case west:
                gc.setFill(Paint.valueOf("blue"));
                break;

        }
        double pos = time/ INTERVAL- color;
        if(id ==15) {
            //gc.fillText("Here we are " + pos, 0, 100);
        }
        while (pos >=0){
            //gc.fillText("Here we are",count++,100);
            switch(direction){
                case north:
                    gc.fillRoundRect(locX*Main.GRIDX +Main.BUFFERX, locY*Main.GRIDY+Main.BUFFERY-(pos)*Main.GRIDY, Main.GRIDX-Main.BUFFERX*2, length*Main.GRIDY-Main.BUFFERY*2,Main.GRIDX,Main.GRIDY);
                    //gc.fillOval(locX*Main.GRIDX +Main.BUFFERX, locY*Main.GRIDY+Main.BUFFERY+(time/INTERVAL-color-1)*Main.GRIDY, Main.GRIDX-Main.BUFFERX, Main.GRIDY-Main.BUFFERY);
                    break;
                case east:
                    gc.fillRoundRect(locX*Main.GRIDX +Main.BUFFERX+(pos-length+1)*Main.GRIDX, locY*Main.GRIDY+Main.BUFFERY, length*Main.GRIDX-Main.BUFFERX*2, Main.GRIDY-Main.BUFFERY*2,Main.GRIDX,Main.GRIDY);
                    //gc.fillOval(locX*Main.GRIDX +Main.BUFFERX+(time/INTERVAL-color-1)*Main.GRIDX, locY*Main.GRIDY+Main.BUFFERY, Main.GRIDX-Main.BUFFERX, Main.GRIDY-Main.BUFFERY);
                    break;
                case west:
                    gc.fillRoundRect(locX*Main.GRIDX +Main.BUFFERX-(pos)*Main.GRIDX, locY*Main.GRIDY+Main.BUFFERY, length*(Main.GRIDX)-Main.BUFFERX*2, Main.GRIDY-Main.BUFFERY*2,Main.GRIDX,Main.GRIDY);
                    //gc.fillOval(locX*Main.GRIDX +Main.BUFFERX-(time/INTERVAL-color-1)*Main.GRIDX, locY*Main.GRIDY+Main.BUFFERY, Main.GRIDX-Main.BUFFERX, Main.GRIDY-Main.BUFFERY);

                    break;
                case south:
                    gc.fillRoundRect(locX*Main.GRIDX +Main.BUFFERX, locY*Main.GRIDY+Main.BUFFERY+(pos-length+1)*Main.GRIDY, Main.GRIDX-Main.BUFFERX*2, length*Main.GRIDY-Main.BUFFERY*2,Main.GRIDX,Main.GRIDY);
                    //gc.fillOval(locX*Main.GRIDX +Main.BUFFERX, locY*Main.GRIDY+Main.BUFFERY-(time/INTERVAL-color-1)*Main.GRIDY, Main.GRIDX-Main.BUFFERX, Main.GRIDY-Main.BUFFERY);

                    break;

            }
            pos-=numColors;
        }

    }

    public Position checkCollision(Station st2, int step){


        //first compute the intersection, then we check if both stations have trains there
        int intersect1=0, intersect2=0;

        int intx = 0, inty=0;


        switch (direction){
            case north:
            case south:
                intx = locX;
                inty = st2.locY;
                intersect1 = st2.locY - locY;
                if (intersect1<0){
                    intersect1 = - intersect1;
                }
                intersect2 = locX-st2.locX;
                if(intersect2<0){
                    intersect2 = - intersect2;
                }



                break;
            case west:
            case east:
                intx = st2.locX;
                inty = locY;
                intersect1 = st2.locX- locX;
                if (intersect1<0){
                    intersect1 = - intersect1;
                }
                intersect2 = locY-st2.locY;
                if(intersect2<0){
                    intersect2 = - intersect2;
                }
                break;
        }
        int pos1 = step - color;
        int pos2 = step - st2.color;
        boolean collide1 =false;

        //check if this station has a train in the intersection
        while (pos1>0){
            for(int i = 0; i < length; i++){
                if((pos1 == intersect1+i)){
                    collide1 = true;
                    break;
                }
            }
            if (collide1)break;
            pos1 -= numColors;
        }
        if(!collide1){
            return null;
        }
        while(pos2>0){
            for(int i = 0; i < length; i++){
                if((pos2 == intersect2+i)){
                    return new Position(intx, inty);
                }
            }
            pos2 -= numColors;
        }
        return null;
    }


}
