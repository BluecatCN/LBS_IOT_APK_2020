package com.example.lbs_tester10;

public class RoomContextState {
    private String room;
    private String status;
    private int light;
    private float noise;

    public RoomContextState(String room, String status, int light, float noise) {
        super();
        this.room = room;
        this.status = status;
        this.light = light;
        this.noise = noise;
    }

    public String getRoom() {
        return this.room;
    }

    public String getLightStatus() {
        return this.status;
    }

    public int getLightLevel() {
        return this.light;
    }

    public float getNoiseLevel() {
        return this.noise;
    }

}
