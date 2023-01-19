package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Player> getAllFilter(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience,
                                     Integer maxExperience, Integer minLevel, Integer maxLevel) {
        List<Player> players = playerRepository.findAll();
        if (name != null) {
            players = players.stream()
                    .filter(nam -> nam.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (title != null) {
            players = players.stream()
                    .filter(tit -> tit.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (race != null) {
            players = players.stream()
                    .filter(rac -> rac.getRace() == race)
                    .collect(Collectors.toList());
        }

        if (profession != null) {
            players = players.stream()
                    .filter(pro -> pro.getProfession() == profession)
                    .collect(Collectors.toList());
        }

        if (after != null) {
            players = players.stream()
                    .filter(af -> af.getBirthday().getTime() >= after)
                    .collect(Collectors.toList());
        }

        if (before != null) {
            players = players.stream()
                    .filter(bef -> bef.getBirthday().getTime() <= before)
                    .collect(Collectors.toList());
        }

        if (banned != null) {
            if (banned) {
                players = players.stream()
                        .filter(ban -> ban.getBanned())
                        .collect(Collectors.toList());
            } else {
                players = players.stream()
                        .filter(ban -> !ban.getBanned())
                        .collect(Collectors.toList());
            }
        }

        if (minExperience != null) {
            players = players.stream()
                    .filter(nexp -> nexp.getExperience() >= minExperience)
                    .collect(Collectors.toList());
        }

        if (maxExperience != null) {
            players = players.stream()
                    .filter(xexp -> xexp.getExperience() <= maxExperience)
                    .collect(Collectors.toList());
        }

        if (minLevel != null) {
            players = players.stream()
                    .filter(nlev -> nlev.getLevel() >= minLevel)
                    .collect(Collectors.toList());
        }

        if (maxLevel != null) {
            players = players.stream()
                    .filter(xlev -> xlev.getLevel() <= maxLevel)
                    .collect(Collectors.toList());
        }

        return players;
    }

    @Override
    public Player getById(Long id) {
        Optional<Player> player=playerRepository.findById(id);
        return player.orElse(null);
    }

    @Override
    public Integer getPlayersCount(String name, String title, Race race, Profession profession, Long after, Long before,
                                   Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        return getAllFilter(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel).size();
    }

    @Override
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

//    @Override
//    public Player createPlayer(Player player) {
//       return playerRepository.save(player);
//    }

//    @Override
//    public Player updatePlayer(Player player, Long id) {
//
//
//        return savePlayer(playerNew);
//    }

    @Override
    public Player savePlayer(Player player) {
        player.setLevel((int) (Math.floor((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100)));
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
        if (player.getBanned() == null) player.setBanned(false);
        playerRepository.save(player);
        return player;
    }

}
