package br.com.gubee.interview.core.features.hero;


import br.com.gubee.interview.core.utils.HeroMapper;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dtos.HeroDtoRequest;
import br.com.gubee.interview.model.dtos.HeroDtoResponse;
import br.com.gubee.interview.model.enums.Race;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class HeroServiceImplTest {

    @Mock
    private HeroRepository heroRepository;

    @Mock
    private HeroMapper heroMapper;


    private HeroServiceImpl heroService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
         heroService = new HeroServiceImpl(heroRepository, heroMapper);
    }

    @Test
    public void testFindAll() {
        List<Hero> heroes = createListHeroes();

        when(heroRepository.findAll()).thenReturn(heroes);
        when(heroMapper.mapToDtoResponseList(heroes)).thenReturn(Arrays.asList(new HeroDtoResponse(), new HeroDtoResponse()));

        List<HeroDtoResponse> heroesResponse = heroService.findAll();

        assertEquals(2, heroesResponse.size());
    }


    @Test
    public void testCompareHeroes() {
        List<Hero> heroes = createListHeroes();

        when(heroRepository.findById("1")).thenReturn(Optional.ofNullable(heroes.get(0)));
        when(heroRepository.findById("2")).thenReturn(Optional.ofNullable(heroes.get(1)));
        when(heroMapper.mapToDtoResponse(heroes.get(0))).thenReturn(new HeroDtoResponse(heroes.get(0).getId().toString(), heroes.get(0).getName(), heroes.get(0).getRace(), heroes.get(0).getPowerStats()));
        when(heroMapper.mapToDtoResponse(heroes.get(1))).thenReturn(new HeroDtoResponse(heroes.get(1).getId().toString(), heroes.get(1).getName(), heroes.get(1).getRace(), heroes.get(1).getPowerStats()));


        Map<String, Object> result = heroService.compare("1", "2");
        assertEquals("1", result.get("id1"));
        assertEquals("2", result.get("id2"));
        assertEquals(50, result.get("strengthDiff"));
        assertEquals(50, result.get("agilityDiff"));
        assertEquals(-50, result.get("dexterityDiff"));
        assertEquals(-50, result.get("intelligenceDiff"));
    }

    @Test
    public void shouldReturnHeroDtoResponseWhenFindById() {
        List<Hero> heroes = createListHeroes();

        when(heroRepository.findById("1")).thenReturn(Optional.ofNullable(heroes.get(0)));
        when(heroMapper.mapToDtoResponse(heroes.get(0))).thenReturn(new HeroDtoResponse(heroes.get(0).getId().toString(), heroes.get(0).getName(), heroes.get(0).getRace(), heroes.get(0).getPowerStats()));

        HeroDtoResponse hero = heroService.findById("1");


        assertEquals("1", hero.getId());
        assertEquals("Superman", hero.getName());
        assertEquals(Race.ALIEN, hero.getRace());
        assertEquals(100, hero.getPowerStats().getStrength());
        assertEquals(100, hero.getPowerStats().getAgility());
        assertEquals(50, hero.getPowerStats().getDexterity());
        assertEquals(50, hero.getPowerStats().getIntelligence());
    }

    @Test
    public void shouldReturnHeroDtoResponseWhenFindByName() {
        List<Hero> heroes = createListHeroes();

        when(heroRepository.findByNameContainingIgnoreCase("Superman")).thenReturn(heroes);
        when(heroMapper.mapToDtoResponseList(heroes)).thenReturn(Arrays.asList(new HeroDtoResponse(), new HeroDtoResponse()));

        List<HeroDtoResponse> heroesResponse = heroService.findByName("Superman");

        assertEquals(2, heroesResponse.size());
    }

    @Test
    public void shouldReturnVoidBodyWhenFindByNameDontMatch(){
        List<Hero> heroes = new ArrayList<>();

        when(heroRepository.findByNameContainingIgnoreCase("test")).thenReturn(heroes);
        when(heroMapper.mapToDtoResponseList(heroes)).thenReturn(new ArrayList<>());

        List<HeroDtoResponse> heroesResponse = heroService.findByName("Superman");
        assertEquals(heroesResponse.size(), 0);
    }

    @Test
    public void shouldReturnHeroDtoResponseWhenInsert() {
        Hero hero = createHero();
        HeroDtoRequest heroDtoRequest = heroMapper.mapToDtoRequest(hero);
        HeroDtoResponse expectedResponse = createHeroDtoResponse();

        when(heroMapper.mapToHero(heroDtoRequest)).thenReturn(hero);
        when(heroRepository.save(hero)).thenReturn(hero);
        when(heroMapper.mapToDtoResponse(hero)).thenReturn(expectedResponse);

        HeroDtoResponse heroResponse = heroService.save(heroDtoRequest);

        assertEquals(expectedResponse.getId(), heroResponse.getId());
        assertEquals(expectedResponse.getName(), heroResponse.getName());
        assertEquals(expectedResponse.getRace(), heroResponse.getRace());
        assertEquals(expectedResponse.getPowerStats(), heroResponse.getPowerStats());
    }

    public List<Hero> createListHeroes(){
        List<Hero> heroes = new ArrayList<>();

        PowerStats powerStats1 = new PowerStats(100, 100, 50, 50);
        PowerStats powerStats2 = new PowerStats(50, 50, 100, 100);

        Hero hero1 = new Hero("1", "Superman", Race.ALIEN, powerStats1, true);
        Hero hero2 = new Hero( "2", "Batman", Race.HUMAN, powerStats2, true);
        heroes.addAll(Arrays.asList(hero1, hero2));
        return heroes;
    }

    public Hero createHero() {
        PowerStats powerStats = new PowerStats(100, 100, 50, 50);
        Hero hero = new Hero("1", "Superman", Race.ALIEN, powerStats, true);
        return hero;
    }

    public HeroDtoResponse createHeroDtoResponse() {
        PowerStats powerStats = new PowerStats(100, 100, 50, 50);

        return new HeroDtoResponse("1", "Superman", Race.ALIEN, powerStats);
    }
}




