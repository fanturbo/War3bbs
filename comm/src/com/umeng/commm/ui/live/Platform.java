package com.umeng.commm.ui.live;

import java.util.List;

/**
 * Created by turbo on 2016/8/9.
 */
public class Platform {

    public Platform() {

    }

    public Platform(String name,List<PlayerInfo> players) {
        this.name = name;
        this.players = players;
    }

    private String name;
    private List<PlayerInfo> players;

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInfo> players) {
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
