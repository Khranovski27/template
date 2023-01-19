package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {
    private static final long BIRTHDAY_MIN_TIME =946674000000L;
    private static final long BIRTHDAY_MAX_TIME =32503669200000L;

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "")
    public ResponseEntity<?> getAll(@RequestParam(required = false, value = "name") String name,
                                    @RequestParam(required = false, value = "title") String title,
                                    @RequestParam(required = false, value = "race") String race,
                                    @RequestParam(required = false, value = "profession") String profession,
                                    @RequestParam(required = false, value = "after") Long after,
                                    @RequestParam(required = false, value = "before") Long before,
                                    @RequestParam(required = false, value = "banned") Boolean banned,
                                    @RequestParam(required = false, value = "minExperience") Integer minExperience,
                                    @RequestParam(required = false, value = "maxExperience") Integer maxExperience,
                                    @RequestParam(required = false, value = "minLevel") Integer minLevel,
                                    @RequestParam(required = false, value = "maxLevel") Integer maxLevel,
                                    @RequestParam(required = false, value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                    @RequestParam(required = false, value = "pageSize", defaultValue = "3") Integer pageSize,
                                    @RequestParam(required = false, value = "order", defaultValue = "ID") String order) {
        Race race1 = null;
        if (race != null) race1 = Race.valueOf(race);

        Profession profession1 = null;
        if (profession != null) profession1 = Profession.valueOf(profession);

        List<Player> players = playerService.getAllFilter(name, title, race1, profession1, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);
        switch (order) {
            case "ID":
                Collections.sort(players, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return Long.compare(o1.getId(), o2.getId());
                    }
                });
                break;
            case "NAME":
                Collections.sort(players, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                break;
            case "EXPERIENCE":
                Collections.sort(players, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return o1.getExperience().compareTo(o2.getExperience());
                    }
                });
                break;
            case "BIRTHDAY":
                Collections.sort(players, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return o1.getBirthday().compareTo(o2.getBirthday());
                    }
                });
                break;
            case "LEVEL":
                Collections.sort(players, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return o1.getLevel().compareTo(o2.getLevel());
                    }
                });

        }
        List<Player> otvPlayers = new ArrayList<>();
        int buff = pageSize;
        for (int i = pageNumber * pageSize; i < players.size(); i++) {
            otvPlayers.add(players.get(i));
            buff--;
            if (buff == 0) break;
        }

        return ResponseEntity.ok(otvPlayers);
    }

    //
    @GetMapping(value = "/count")
    public ResponseEntity<?> getPlayersCount(@RequestParam(required = false, value = "name") String name,
                                             @RequestParam(required = false, value = "title") String title,
                                             @RequestParam(required = false, value = "race") String race,
                                             @RequestParam(required = false, value = "profession") String profession,
                                             @RequestParam(required = false, value = "after") Long after,
                                             @RequestParam(required = false, value = "before") Long before,
                                             @RequestParam(required = false, value = "banned") Boolean banned,
                                             @RequestParam(required = false, value = "minExperience") Integer minExperience,
                                             @RequestParam(required = false, value = "maxExperience") Integer maxExperience,
                                             @RequestParam(required = false, value = "minLevel") Integer minLevel,
                                             @RequestParam(required = false, value = "maxLevel") Integer maxLevel) {
        Race race1 = null;
        if (race != null) race1 = Race.valueOf(race);

        Profession profession1 = null;
        if (profession != null) profession1 = Profession.valueOf(profession);

        return ResponseEntity.ok(playerService.getPlayersCount(name, title, race1, profession1, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Player> deletePlayerId(@PathVariable("id") String id) throws Exception {
        long idee = 0;
        try {
            idee = Long.parseLong(id);
            if (idee < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player player = this.playerService.getById(idee);

        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        this.playerService.delete(idee);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Player> getPlayerId(@PathVariable("id") String id) throws Exception {
        long idee = 0L;
        try {
            idee = Long.parseLong(id);
            if (idee <= 0L) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player player = this.playerService.getById(idee);

        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player,HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Player> createPlayer(@RequestBody @Validated Player player){
        if(player==null||player.getName()==null||player.getName().length()<1||player.getName().length()>12||
                         player.getTitle()==null||player.getTitle().length()<1||player.getTitle().length()>30||
                         player.getRace()==null||player.getProfession()==null||
                         player.getExperience()<0||player.getExperience()>10000000||
                         player.getBirthday().getTime()<BIRTHDAY_MIN_TIME||player.getBirthday().getTime()>BIRTHDAY_MAX_TIME){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
         player.setId(41L);
        this.playerService.savePlayer(player);
        return new ResponseEntity<>(player,HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@RequestBody @Validated Player player, @PathVariable("id") String id){
        long idee = 0L;
        try {
            idee = Long.parseLong(id);
            if (idee <= 0L || player==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player playerNew = playerService.getById(idee);
        if (playerNew == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (player.getName() != null) {
            if(player.getName().length()<1||player.getName().length()>12){
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            playerNew.setName(player.getName());
        }
        if (player.getTitle() != null) {
            if(player.getTitle()==null||player.getTitle().length()<1||player.getTitle().length()>30){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            playerNew.setTitle(player.getTitle());
        }
        if (player.getRace() != null) playerNew.setRace(player.getRace());
        if (player.getProfession() != null) playerNew.setProfession(player.getProfession());
        if (player.getBirthday() != null) {
            if(player.getBirthday().getTime()<BIRTHDAY_MIN_TIME||player.getBirthday().getTime()>BIRTHDAY_MAX_TIME){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            playerNew.setBirthday(player.getBirthday());
        }
        if (player.getBanned() != null) playerNew.setBanned(player.getBanned());
        if (player.getExperience() != null) {
            if(player.getExperience()<0||player.getExperience()>10000000){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            playerNew.setExperience(player.getExperience());
        }

        return new ResponseEntity<>(playerService.savePlayer(playerNew),HttpStatus.OK);
    }
    //Not found GET /rest/players?name=aaaa&title=bbbb&race=HUMAN&profession=WARRIOR&after=1672531200000&before=1675123200000&banned=false&minExperience=1000&maxExperience=2000& minLevel=1&maxLevel=2&pageNumber=0&pageSize=5&order=ID
}