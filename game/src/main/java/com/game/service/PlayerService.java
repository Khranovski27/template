package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PlayerService {

    List<Player> getAllFilter(String name, String title, Race race, Profession profession, Long after, Long before,
                              Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel);

    Player getById(@NonNull Long id);

    Integer getPlayersCount(String name, String title, Race race, Profession profession, Long after, Long before,
                            Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel);

    void delete(Long id);

//    Player createPlayer(Player player);

  //  Player updatePlayer(Player player,Long id);

    Player savePlayer(Player player);

}
