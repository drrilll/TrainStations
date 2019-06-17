package sample;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Controller {
    int numStations = 6;

    //12 stations
    //3 6 5 0 7 2 1 4 3 6 5 0
    //2 3 0 1 6 7 4 5 2 3 0 1
    //4 1 2 7 0 5 6 3 4 1 2 7
    //5 4 7 6 1 0 3 2 5 4 7 6

    //order of coloring is north, east, south west
    // 6 square
    //int[] coloring = {6,7,4,4,5,5,4,5,0,1,2,1,5,4,7,6,7,0,7,0,1,0,3,2};
    //int[] coloring = {6,7,4,4,5,5,4,5,0,0,1,1,5,4,7,6,7,0,7,0,1,0,3,2};

    //6 diagonal
    int[][] coloring = {{4,6,4,6,4,4},{0,2,0,2,0,2},{4,4,6,4,6,4},{2,0,2,0,2,0}};
    ArrayList<Integer> ncolor, ecolor, wcolor,scolor;
    //4 square
    //int[] coloring = {2,3,0,1,0,1,2,3,7,6,5,4,5,4,7,6};

    //8 square
    //int[][] coloring = {{0,5,2,3,0,1,0,7},{2,3,0,1,6,7,4,5},{7,2,1,4,3,6,5,0},{2,7,0,5,6,3,4,5}};

    ArrayList<Station> northbound, southbound, eastbound, westbound;
    int direction = 1; int oldDirection = 1;
    int time = 0, oldtime = 0;
    //should be the time to travel one grid cell
    public static final int INTERVAL = 60;
    public enum Layout {square, diagonal};

    Layout layout = Layout.square;
    GraphicsContext gc;
    Main main;
    ArrayList<Position> collisions = new ArrayList<>();


    Timeline gameloop;
    public Controller(Main main, Timeline gameloop, GraphicsContext gc){
        this.gameloop = gameloop; this.gc = gc; this.main = main;
    }

    public void setNumStations(int num){
        numStations = num;
    }

    public void initialize()  {
        //stations = new ArrayList<>(numStations*4);
        northbound = new ArrayList<>(numStations);
        southbound= new ArrayList<>(numStations);
        eastbound= new ArrayList<>(numStations);
        westbound= new ArrayList<>(numStations);
        //the stations are created in the following order:
        // left to right going up, right, down, left


        //stations going north

        for (int i = 0; i < numStations; i++) {
            if(i < coloring[0].length) {
                northbound.add(new Station(i + 1, 2 * numStations + 1, coloring[0][i], Station.Direction.north));
                eastbound.add(new Station(0, 2 * numStations - (i), coloring[1][i], Station.Direction.east));
                southbound.add(new Station(numStations + 1 + i, 0, coloring[2][i], Station.Direction.south));
                westbound.add(new Station(numStations * 2 + 1, 1 + i, coloring[3][i], Station.Direction.west));
            }else{
                northbound.add(new Station(i + 1, 2 * numStations + 1, 0, Station.Direction.north));
                eastbound.add(new Station(0, 2 * numStations - (i), 0, Station.Direction.east));
                southbound.add(new Station(numStations + 1 + i, 0, 0, Station.Direction.south));
                westbound.add(new Station(numStations * 2 + 1, 1 + i, 0, Station.Direction.west));
            }
        }

        updateStationPositions();
        update();


    }

    public void togglePaused(){
        if(direction == 0){
            direction = oldDirection;
            gameloop.play();
        }else{
            direction = 0;
            gameloop.pause();
        }
    }




    public void reverse() {
        direction = -direction;
        oldDirection = -oldDirection;
    }


    public void setTime(Number new_val) {
        time = new_val.intValue()*INTERVAL;
        checkCollisions(new_val.intValue());
    }

    public void pause() {
        direction = 0;
        gameloop.pause();
    }

    public void passTheTime() {
        time += direction;
        if((time - oldtime)>=INTERVAL){
            oldtime = time;
            checkCollisions(time/INTERVAL);
        }
    }

    public void checkCollisions(int step){
        main.collisionOff();
        collisions.clear();
        Position pos;
        for(Station nbound: northbound){
            for (Station wbound: westbound){
                pos = nbound.checkCollision(wbound,step);
                if(pos!=null){
                    //main.collisionOn(pos);
                    //return;
                    collisions.add(pos);
                }
            }
            for (Station ebound: eastbound){
                pos = nbound.checkCollision(ebound,step);

                if(pos!= null){
                    //main.collisionOn(pos);
                    //return;
                    collisions.add(pos);
                }
            }
        }
        for(Station nbound: southbound){
            for (Station wbound: westbound){
                pos = nbound.checkCollision(wbound,step);

                if(pos!=null){
                    //main.collisionOn(pos);
                    //return;
                    collisions.add(pos);

                }
            }
            for (Station ebound: eastbound){
                pos = nbound.checkCollision(ebound,step);

                if(pos != null){
                    //main.collisionOn(pos);
                    //return;
                    collisions.add(pos);
                }
            }
        }
        if (collisions.size()>0){
            main.collisionOn(collisions);
        }
    }

    public void stepBack() {
        pause();
        time -= INTERVAL;
        time -= Math.floorMod(time,INTERVAL);
        checkCollisions(time/INTERVAL);
        update();
    }

    public void stepForward() {
        pause();
        time+=INTERVAL;
        time -= Math.floorMod(time,INTERVAL);
        checkCollisions(time/INTERVAL);
        update();
    }

    public void changeLayout(Layout layout){
        if (layout == this.layout){
            return;
        }
        this.layout = layout;
        updateStationPositions();
        update();
    }

    /**
     * We have changed the layout and need to update the station locations
     */
    private void updateStationPositions() {
        //stations going east
        if(layout == Layout.square) {
            for (int i = 0; i < numStations; i++) {
                northbound.get(i).setPosition(i+1,2*numStations+1);
                eastbound.get(i).setPosition(0,numStations+(i+1));
                southbound.get(i).setPosition(numStations+1+i,0);
                westbound.get(i).setPosition(numStations*2+1,1+i);
            }
        }else{
            for (int i = 0; i < numStations; i++) {
                eastbound.get(i).setPosition(i,2*numStations+i);
                northbound.get(i).setPosition(numStations+i,3*numStations+i);
                southbound.get(i).setPosition(2*numStations+i,i);
                westbound.get(i).setPosition(3*numStations+i,numStations+i);
            }
        }

    }

    public void update() {
        gc.clearRect(0, 0, Main.SIZEX,Main.SIZEY);
        for (Station station: northbound){
            station.draw(gc, time);
        }
        for (Station station: eastbound){
            station.draw(gc, time);
        }
        for (Station station: southbound){
            station.draw(gc, time);
        }
        for (Station station: westbound){
            station.draw(gc, time);
        }
    }

    public void setNorthColors(String colors){
        String[] col = colors.split(" ");
        int col1=0;
        for (int i = 0; i < numStations; i++){
            if (i>=col.length){
                northbound.get(i).setColor(0);
            }else {
                col1 = Integer.parseInt(col[i]);
                if (col1>Station.numColors){
                    col1 = 0;
                }
                northbound.get(i).setColor(col1);
            }
        }

        update();
    }

    public void setEastColors(String colors){
        String[] col = colors.split(" ");
        int col1=0;

        for (int i = 0; i < numStations; i++){
            if (i>=col.length){
                eastbound.get(i).setColor(0);
            }else {
                col1 = Integer.parseInt(col[i]);
                if (col1>Station.numColors){
                    col1 = 0;
                }
                eastbound.get(i).setColor(col1);
            }
        }
        update();

    }

    public void setSouthColors(String colors){
        String[] col = colors.split(" ");
        int col1=0;

        for (int i = 0; i < numStations; i++){
            if (i>=col.length){
                southbound.get(i).setColor(0);
            }else {
                col1 = Integer.parseInt(col[i]);
                if (col1>Station.numColors){
                    col1 = 0;
                }
                southbound.get(i).setColor(col1);
            }
        }
        update();

    }

    public void setWestColors(String colors){
        String[] col = colors.split(" ");
        int col1=0;

        for (int i = 0; i < numStations; i++){
            if (i>=col.length){
                westbound.get(i).setColor(0);
            }else {
                col1 = Integer.parseInt(col[i]);
                if (col1>Station.numColors){
                    col1 = 0;
                }
                westbound.get(i).setColor(col1);
            }
        }
        update();

    }

    public void setCarLength(int length){
        Station.setLength(length);
        update();
    }


    public void optimalColouring(){
        int len = Station.length;
        int colors = 2*len*len;
        Station.setColors(colors);
        for (Station station: northbound){
            int color = -(station.locX + len*(station.locX%len)+station.locY+2*len);
            color = color % colors;
            if (color < 0){
                color+=colors;
            }
            station.setColor(color);
        }
        for (Station station: southbound){
            int color = (station.locX + len*(station.locX%len)+station.locY+2*len);
            color = color % colors;
            if (color < 0){
                color+=colors;
            }
            station.setColor(color);
        }
        for (Station station: eastbound){
            int color = (station.locY - len*(station.locY%len)+station.locX+len);
            color = color % colors;
            if (color < 0){
                color+=colors;
            }
            station.setColor(color);
        }
        for (Station station: westbound){
            int color = -(station.locY - len*(station.locY%len)+station.locX+len);
            color = color % colors;
            if (color < 0){
                color+=colors;
            }
            station.setColor(color);
        }
        update();
    }


}
