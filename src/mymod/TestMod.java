package mymod;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.codedisaster.steamworks.SteamAPI;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.Settings.GameLanguage;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PotionBelt;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.ISubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.PreUpdateSubscriber;
import basemod.interfaces.StartGameSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.MaxHPChangeSubscriber;
import cards.*;
import cards.colorless.*;
import cards.mahjong.*;
import christmasMod.mymod.ChristmasMod;
import events.*;
import halloweenMod.mymod.HalloweenMod;
import potions.*;
import powers.TheFatherPower;
import powers.SuperconductorNoEnergyPower;
import relics.*;
import utils.*;

/**
 * @author 彼君不触
 * @version 9/24/2021
 * @since 6/17/2018
 */

@SpireInitializer
public class TestMod implements EditRelicsSubscriber, EditCardsSubscriber, EditStringsSubscriber, StartGameSubscriber,
		PreUpdateSubscriber, PostUpdateSubscriber, PostInitializeSubscriber, PostDungeonInitializeSubscriber,
		OnStartBattleSubscriber, MaxHPChangeSubscriber, EditKeywordsSubscriber, PostBattleSubscriber, MiscMethods {
	public static final String MOD_ID = "testmod";
	public static final String MOD_NAME = "TestMod";
	public static final String SAVE_NAME = "TestMod";
	public static final String SAVE_FILE_NAME = "Common";
	public static final Logger LOGGER = LogManager.getLogger(TestMod.class.getName());
	private ModPanel settingsPanel;
	
	private static final ArrayList<ISubscriber> SUB_MOD = new ArrayList<ISubscriber>();
	
	public static void subscribeSubModClass(ISubscriber sub) {
		SUB_MOD.add(sub);
	}
	
	public static void addRelicsToPool(AbstractRelic... relics) {
		Stream.of(relics).forEach(RELICS::add);
	}
	
	private static void editSubModRelics(ISubscriber sub) {
		if (sub instanceof EditRelicsSubscriber) {
			((EditRelicsSubscriber)sub).receiveEditRelics();
		} else {
			info(sub.getClass().getCanonicalName() + " is not EditRelicsSubscriber! skipped.");
		}
	}
	
	private static void editSubModCards(ISubscriber sub) {
		if (sub instanceof EditCardsSubscriber) {
			((EditCardsSubscriber)sub).receiveEditCards();
		} else {
			info(sub.getClass().getCanonicalName() + " is not EditCardsSubscriber! skipped.");
		}
	}
	
	private static void editSubModKeywords(ISubscriber sub) {
		if (sub instanceof EditKeywordsSubscriber) {
			((EditKeywordsSubscriber)sub).receiveEditKeywords();
		} else {
			info(sub.getClass().getCanonicalName() + " is not EditKeywordsSubscriber! skipped.");
		}
	}
	
	private static void editSubModStrings(ISubscriber sub) {
		if (sub instanceof EditStringsSubscriber) {
			((EditStringsSubscriber)sub).receiveEditStrings();
		} else {
			info(sub.getClass().getCanonicalName() + " is not EditStringsSubscriber! skipped.");
		}
	}
	
	private static void editSubModPostDungeonInit(ISubscriber sub) {
		if (sub instanceof PostDungeonInitializeSubscriber) {
			((PostDungeonInitializeSubscriber)sub).receivePostDungeonInitialize();
		} else {
			info(sub.getClass().getCanonicalName() + " is not PostDungeonInitializeSubscriber! skipped.");
		}
	}
	
	private static void editSubModPostUpdate(ISubscriber sub) {
		if (sub instanceof PostUpdateSubscriber) {
			((PostUpdateSubscriber)sub).receivePostUpdate();
		} else {
			//info(sub.getClass().getCanonicalName() + " is not PostUpdateSubscriber! skipped.");
		}
	}
	
	private static void editSubModStartGame(ISubscriber sub) {
		if (sub instanceof StartGameSubscriber) {
			((StartGameSubscriber)sub).receiveStartGame();
		} else {
			info(sub.getClass().getCanonicalName() + " is not StartGameSubscriber! skipped.");
		}
	}
	
	private static void editSubModStartBattle(ISubscriber sub, AbstractRoom r) {
		if (sub instanceof OnStartBattleSubscriber) {
			((OnStartBattleSubscriber)sub).receiveOnBattleStart(r);
		} else {
			info(sub.getClass().getCanonicalName() + " is not OnStartBattleSubscriber! skipped.");
		}
	}
	
	private static void editSubModPostBattle(ISubscriber sub, AbstractRoom r) {
		if (sub instanceof PostBattleSubscriber) {
			((PostBattleSubscriber)sub).receivePostBattle(r);
		} else {
			info(sub.getClass().getCanonicalName() + " is not PostBattleSubscriber! skipped.");
		}
	}

	@Override
	public void receiveEditKeywords() {
		// TODO
		switch (Settings.language) {
		case ZHS:
		case ZHT:
			BaseMod.addKeyword(new String[] { "死亡刻印" }, "被标记 #y死亡刻印 的敌人在失去生命时会增加等量的层数。");
			break;
		default:
			BaseMod.addKeyword(new String[] { "Imprint" }, "#yImprint will increase the amount of damage whenever the owner loses HP.");
			break;
		}
		SUB_MOD.forEach(TestMod::editSubModKeywords);
	}

	private static class RoomTrigger {
		AbstractRoom r;
		RoomTrigger(AbstractRoom r) {
			this.r = r;
		}
		void postTrigger(ISubscriber s) {
			editSubModPostBattle(s, this.r);
		}
		void preTrigger(ISubscriber s) {
			editSubModStartBattle(s, this.r);
		}
	}
	
	@Override
	public void receivePostBattle(AbstractRoom r) {
		SUB_MOD.forEach(new RoomTrigger(r)::postTrigger);
	}
	
	public static void info(String s) {
		LOGGER.info(s);
	}
	
	public static boolean isLocalTest() {
		return "280 chan".equals(CardCrawlGame.playerName) && !SteamAPI.isSteamRunning();
	}
	
	public static String makeID(String id) {
		if (id.length() > TestMod.MOD_ID.length() + 1 && id.substring(0, TestMod.MOD_ID.length()).equals(MOD_ID)) {
			info("这个为什么id重复了前缀:" + id);
			return id;
		}
		return MOD_ID + "-" + id;
	}
	
	private static String unMakeID(String newID) {
		return newID.substring(TestMod.MOD_ID.length() + 1);
	}
	
	private static void checkOldID() {
		RELICS.stream().map(r -> r.relicId).forEach(TestMod::checkOldRelicID);
		CARDS.stream().map(c -> c.cardID).forEach(TestMod::checkOldCardID);
		if (UnlockTracker.isCardSeen("Collecter"))
			UnlockTracker.markCardAsSeen(makeID("Collector"));
		if (UnlockTracker.isCardSeen("LackOfEnergy"))
			UnlockTracker.markCardAsSeen(makeID("PocketStoneCalender"));
		if (UnlockTracker.isCardSeen("DorothysBlackCat") || UnlockTracker.isCardSeen(makeID("DorothysBlackCat")))
			UnlockTracker.markCardAsSeen(makeID("TaurusBlackCat"));
		Stream.of(Sins.SINS).filter(c -> c instanceof AbstractTestCard).map(c -> c.cardID)
				.forEach(TestMod::checkOldCardID);
		MAHJONGS.stream().map(c -> c.cardID).forEach(TestMod::checkOldCardID);
	}
	
	private static void checkOldRelicID(String id) {
		if (UnlockTracker.isRelicSeen(unMakeID(id)))
			UnlockTracker.markRelicAsSeen(id);
	}
	
	private static void checkOldCardID(String id) {
		if (UnlockTracker.isCardSeen(unMakeID(id)))
			UnlockTracker.markCardAsSeen(id);
	}
	
	private static String langPrefix = null;
	
	public static String lanPrefix(String s) {
		if (langPrefix != null)
			return langPrefix + s;
		// TODO
		switch (Settings.language) {
		case ZHS:
		case ENG:
			langPrefix = Settings.language.name().toLowerCase() + "/";
			break;
		case ZHT:
			langPrefix = "zhs/";
			break;
		default:
			langPrefix = "eng/";
			break;
		}
		return langPrefix + s;
	}
	
	public static String stringsPathFix(String s) {
		return "resources/strings/" + s + ".json";
	}

	public static String readString(String type) {
		return Gdx.files.internal(stringsPathFix(lanPrefix(type))).readString(String.valueOf(StandardCharsets.UTF_8));
	}
	
	/**
	 * Entrance
	 */
	public static void initialize() {
		BaseMod.subscribe(new TestMod());
		HalloweenMod.initialize();
		ChristmasMod.initialize();
	}
	
	private void initLatest() {
		addLatest(new Dye(), new Restrained(), new RandomTest(), new GoldenSoul(), new GremlinBalance(),
				new IWantAll(), new TemporaryBarricade(), new ShadowAmulet(), new HyperplasticTissue(),
				new VentureCapital());
		BAD_RELICS = MY_RELICS.stream().filter(AbstractTestRelic::isBad).collect(this.collectToArrayList());
		addLatest(new VirtualReality(), new WeaknessCounterattack(), new Plague(), new Reproduce(),
				new HandmadeProducts(), new Automaton(), new PowerStrike());
	}

	private static void addRelic(AbstractRelic r) {
		BaseMod.addRelic(r, RelicType.SHARED);
	}
	
	@Override
	public void receiveEditRelics() {
		// 添加遗物进游戏
		RELICS = Stream.of(new ObsoleteBoomerang(), new D_4(), new BlackFramedGlasses(), new BouquetWithThorns(),
				new PortableAltar(), new Sins(), new Register(), new EnergyCheck(), new Mahjong(), new CasingShield(),
				new OneHitWonder(), new Iteration(), new Temperance(), new Justice(), new Fortitude(), new Charity(),
				new Hope(), new AssaultLearning(), new Muramasa(), new HeartOfDaVinci(), new IncinerationGenerator(),
				new IndustrialRevolution(), new AscensionHeart(), new RealStoneCalender(), new Nyarlathotep(),
				new BalancedPeriapt(), new MagicalMallet(), new Laevatain(), new TimeTraveler(), new Prudence(),
				new Maize(), new DragonStarHat(), new Nine(), new SaigiyounoYou(), new Motorcycle(), new DreamHouse(),
				new CrystalShield(), new Alchemist(), new StringDisintegrator(), new HarvestTotem(), new LifeArmor(),
				new BatchProcessingSystem(), new CardMagician(), new NegativeEmotionEnhancer(), new Brilliant(),
				new IWantAll(), new Antiphasic(), new IntensifyImprint(), new KeyOfTheVoid(), new ThousandKnives(),
				new Faith(), new FatalChain(), new CyclicPeriapt(), new EqualTreatment(), new ConstraintPeriapt(),
				new InjuryResistance(), new DeterminationOfClimber(), new Déjàvu(), new TestBox(), new VoidShard(),
				new BloodSacrificeSpiritualization(), new Acrobat(), new ArcanaOfDestiny(), new InfectionSource(),
				new TheFather(), new Fanaticism(), new TurbochargingSystem(), new HeartOfStrike(),
				new RainbowHikingShoes(), new GoldenSoul(), new RandomTest(), new TemporaryBarricade(),
				new GremlinBalance(), new RetroFilter(), new DominatorOfWeakness(), new ShadowAmulet(),
				new HyperplasticTissue(), new TraineeEconomist(), new VentureCapital(), new Dye(), new Restrained())
				.collect(this.collectToArrayList());

		SUB_MOD.forEach(TestMod::editSubModRelics);
		RELICS.forEach(TestMod::addRelic);
		MY_RELICS = RELICS.stream().filter(r -> r instanceof AbstractTestRelic).map(r -> (AbstractTestRelic) r)
				.collect(this.collectToArrayList());
		
		MY_RELICS.forEach(AbstractTestRelic::addToMap);
		
		/*for (int i = 0; i < BOTTLES.size(); i++) {
			BaseMod.registerBottleRelic(BOTTLES.get(i), BOTTLES.get(i));
		}*/
	}

	public static ArrayList<AbstractRelic> RELICS = new ArrayList<AbstractRelic>();
	private static ArrayList<AbstractTestRelic> MY_RELICS = new ArrayList<AbstractTestRelic>();
	//private static ArrayList<AbstractBottleRelic> BOTTLES = new ArrayList<AbstractBottleRelic>();
	
	private void loadStrings(Class<?> c) {
		String s = c.getSimpleName().toLowerCase();
		BaseMod.loadCustomStrings(c, readString(s.substring(0, s.length() - 7) + "s"));
	}
	
	@Override
	public void receiveEditStrings() {
		Stream.of(RelicStrings.class, CardStrings.class, PowerStrings.class, PotionStrings.class, EventStrings.class)
				.forEach(this::loadStrings);
		SUB_MOD.forEach(TestMod::editSubModStrings);
	}

	@Override
	public void receiveEditCards() {
		Stream.of(Sins.SINS).filter(c -> c instanceof AbstractTestCard).forEach(BaseMod::addCard);
		// 添加卡牌进游戏
		CARDS = Stream.of(new DisillusionmentEcho(), new LifeRuler(), new ComboMaster(), new AdversityCounterattack(),
				new SubstituteBySubterfuge(), new Superconductor(), new PulseDistributor(), new EternalityOfKhronos(),
				new AutoReboundSystem(), new Wormhole(), new RepeatForm(), new Plague(), new SunMoon(), new Mystery(),
				new ShutDown(), new Reflect(), new Provocation(), new PocketStoneCalender(), new Recap(), new Dream(),
				new Bloodthirsty(), new Arrangement(), new Collector(), new ChaoticCore(), new SelfRegulatingSystem(),
				new LimitFlipper(), new ConditionedReflex(), new RabbitOfFibonacci(), new BloodBlade(), new TradeIn(),
				new DeathImprint(), new BloodShelter(), new AssimilatedRune(), new TreasureHunter(), new HeadAttack(),
				new PainDetonator(), new FightingIntention(), new Reverberation(), new CardIndex(), new BackupPower(),
				new PerfectCombo(), new Lexicography(), new Librarian(), new VirtualReality(), new HandmadeProducts(),
				new WeaknessCounterattack(), new TemporaryDeletion(), new EnhanceArmerment(), new TaurusBlackCat(),
				new Automaton(), new Illusory(), new Reproduce(), new PowerStrike())
				.collect(this.collectToArrayList());

		CARDS.forEach(BaseMod::addCard);
		
		for (int i = 0; i < 37; i++)
			this.addMahjongToList(AbstractMahjongCard.mahjong(i));
		
		SUB_MOD.forEach(TestMod::editSubModCards);
		
		//BaseMod.addCard(new Test());
	}
	
	private void addMahjongToList(AbstractMahjongCard c) {
		BaseMod.addCard(c);
		MAHJONGS.add(c);
	}

	public static final ArrayList<AbstractMahjongCard> MAHJONGS = new ArrayList<AbstractMahjongCard>();
	public static ArrayList<AbstractCard> CARDS = new ArrayList<AbstractCard>();
	
	public static void removeFromPool(AbstractRelic r) {
		Stream.of(AbstractDungeon.commonRelicPool, AbstractDungeon.uncommonRelicPool, AbstractDungeon.rareRelicPool,
				AbstractDungeon.bossRelicPool, AbstractDungeon.shopRelicPool).forEach(l -> l.remove(r.relicId));
	}
	
	private static void avoidFirstTime() {
		if (TipTracker.pref != null)
			TipTracker.neverShowAgain("RELIC_TIP");
	}
	
	private static ArrayList<AbstractRelic> LATEST = new ArrayList<AbstractRelic>();
	public static ArrayList<AbstractRelic> BAD_RELICS = new ArrayList<AbstractRelic>();
	private static ArrayList<AbstractCard> LATEST_CARD = new ArrayList<AbstractCard>();
	
	public static Object checkLatest(boolean relic) {
		Function<ArrayList<? extends Object>, Object> f = l -> l.isEmpty() ? null : l.remove(0);
		return f.apply(relic ? LATEST : LATEST_CARD);
	}
	
	private void addLatest(AbstractRelic... list) {
		LATEST = Stream.of(list).collect(this.collectToArrayList());
	}
	
	private void addLatest(AbstractCard... list) {
		LATEST_CARD = Stream.of(list).collect(this.collectToArrayList());
	}
	
	@Override
	public void receivePostDungeonInitialize() {
		if (AbstractDungeon.floorNum > 1) {
			return;
		}
		
		checkEnableConsoleMultiplayer();
		
		if (config == null)
			this.initSavingConfig();
		SUB_MOD.forEach(TestMod::editSubModPostDungeonInit);
		TheFatherPower.clear();
		DragonStarHat.resetValue();
		Faith.reset();
		AscensionHeart.reset();
		Mahjong.saveDefaultYama();
		// NoteOfAlchemist.setState(false);
		// 初始遗物
		obtain(AbstractDungeon.player, new TestBox());
	}
	
	public void unlockAll() {
		info("开始解锁");
		CARDS.forEach(this::markAsSeen);
		Stream.of(Sins.SINS).filter(c -> c instanceof AbstractTestCard).forEach(this::markAsSeen);
		MAHJONGS.forEach(this::markAsSeen);
		RELICS.forEach(this::markAsSeen);
	}
	
	private static final ArrayList<AbstractRelic> TO_OBTAIN = new ArrayList<AbstractRelic>();
	
	public static void obtainLater(AbstractRelic r) {
		TO_OBTAIN.add(r);
	}
	
	public AbstractRelic randomBonusRelic() {
		ArrayList<AbstractRelic> unSeen = RELICS.stream().filter(r -> !r.isSeen).collect(this.collectToArrayList());
		Function<ArrayList<AbstractRelic>, AbstractRelic> f = l -> l.get((int) (Math.random() * l.size()));
		return f.apply(unSeen.isEmpty() ? RELICS : unSeen);
	}
	
	public static boolean obtain(AbstractPlayer p, AbstractRelic r, boolean canDuplicate) {
		if (r == null)
			return false;
		if (!p.hasRelic(r.relicId) || canDuplicate) {
			int slot = p.relics.size();
			r.makeCopy().instantObtain(p, slot, true);
			removeFromPool(r);
			return true;
		}
		return false;
	}
	
	public AbstractCard randomBonusCard() {
		ArrayList<AbstractCard> unSeen = CARDS.stream().filter(c -> !c.isSeen).collect(this.collectToArrayList());
		Function<ArrayList<AbstractCard>, AbstractCard> f = l -> l.get((int) (Math.random() * l.size()));
		return f.apply(unSeen.isEmpty() ? CARDS : unSeen);
	}
	
	public static boolean obtain(AbstractPlayer p, AbstractCard c, int timesUpgrade) {
		if (c == null)
			return false;
		AbstractCard temp = c.makeCopy();
		for (int i = 0; i < timesUpgrade && temp.canUpgrade(); i++)
			temp.upgrade();
		AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(temp, Settings.WIDTH / 2.0F,
				Settings.HEIGHT / 2.0F));
		return true;
	}
	
	public static boolean obtain(AbstractPlayer p, Object o) {
		if (o instanceof AbstractCard) {
			return obtain(p, (AbstractCard)o, 0);
		} else if (o instanceof AbstractRelic) {
			return obtain(p, (AbstractRelic)o, false);
		} else {
			info("为什么获得" + o);
		}
		return false;
	}

	@Override
	public void receivePreUpdate() {
		AbstractPlayer p = AbstractDungeon.player;
		if (p != null) {
			p.relics.stream().filter(r -> r instanceof AbstractTestRelic)
					.forEach(r -> ((AbstractTestRelic) r).preUpdate());
			this.updateGlow();
		}
	}
	
	private static void invoke(AbstractTestRelic r, boolean equip) {
		try {
			r.getClass().getMethod(equip ? "equipAction" : "unequipAction").invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}

	private void invokeEquip(AbstractTestRelic r) {
		invoke(r, true);
	}
	
	private void invokeUnequip(AbstractTestRelic r) {
		invoke(r, false);
	}
	
	@Override
	public void receivePostUpdate() {
		SUB_MOD.forEach(TestMod::editSubModPostUpdate);
		// TODO Auto-generated method stub
		AbstractPlayer p = AbstractDungeon.player;
		
		if (p != null) {
			if (!TO_OBTAIN.isEmpty() && AbstractDungeon.topLevelEffects.isEmpty()) {
				obtain(p, TO_OBTAIN.remove(0), true);
			}

			MY_RELICS.stream().map(r -> r.relicId).filter(p::hasRelic).map(p::getRelic).map(r -> (AbstractTestRelic) r)
					.peek(TestMod::setActivity).forEach(TestMod::setShow);

			/*if (p.hasRelic("NoteOfAlchemist") && p.relics.get(0).relicId != "NoteOfAlchemist") {
				if (!NoteOfAlchemist.recorded()) {
					if ((AbstractDungeon.floorNum < 1) || (AbstractDungeon.currMapNode != null
							&& AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss)) {
						NoteOfAlchemist.equipAction();
					} else if (AbstractDungeon.currMapNode != null) {
						NoteOfAlchemist.setState(true);
					}
				}
			} else if (CardCrawlGame.mode == GameMode.GAMEPLAY || CardCrawlGame.mode == GameMode.DUNGEON_TRANSITION) {
				NoteOfAlchemist.setState(false);
			}*/

			MY_RELICS.stream().filter(AbstractTestRelic::tryEquip).forEach(this::invokeEquip);
			MY_RELICS.stream().filter(AbstractTestRelic::tryUnequip).forEach(this::invokeUnequip);
			
			
			p.relics.stream().filter(r -> r instanceof AbstractTestRelic).map(r -> (AbstractTestRelic) r)
					.forEach(AbstractTestRelic::postUpdate);

			/*if (needFix){
				if (p.maxHealth <= 0 && !p.isDead && !p.isDying && !p.halfDead) {
					p.maxHealth = 1;
					p.currentHealth = 1;
				}
			}*/
			
			for (Iterator<PerfectCombo> i = PerfectCombo.TO_UPDATE.iterator(); i.hasNext();) {
				if (AbstractDungeon.currMapNode == null)
					break;
				PerfectCombo c = i.next();
	    		if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
					if (p.hand.contains(c)) {
		        		if (c.magicNumber < c.countUpgrades() + c.misc) {
		        			c.applyPowers();
		        		}
		    		}
	    		} else {
	    			i.remove();
	    		}
			}
			
			for (Iterator<AbstractUpdatableCard> i = AbstractUpdatableCard.TO_UPDATE.iterator(); i.hasNext();) {
				if (AbstractDungeon.currMapNode == null)
					break;
				AbstractUpdatableCard c = i.next();
	    		if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
					if (p.hand.contains(c)) {
						AbstractMonster m = AbstractDungeon.getMonsters().hoveredMonster;
	        			c.preApplyPowers(p, m);
	    				if (this.hasStringDisintegrator())
	    					continue;
	        			c.applyPowers();
		    		} else {
		    			c.resetDescription();
		    		}
	    		} else {
	    			i.remove();
	    		}
			}

			this.turnSkipperUpdate();
			
			BoxForYourself.updateThis();
			
			HeartOfDaVinci.updateThis();
			
			SuperconductorNoEnergyPower.UpdateCurrentInstance();
		}
	}
	
	public static void setShow(AbstractTestRelic r) {
		r.show = AbstractDungeon.player.relics.size() < 26;
	}
	
	public static void setActivity(AbstractTestRelic relic) {
		AbstractDungeon.player.relics.stream().sequential().filter(r -> r.relicId.equals(relic.relicId))
				.map(r -> (AbstractTestRelic) r).peek(AbstractTestRelic::setAsInactive).limit(1)
				.forEach(AbstractTestRelic::setAsActive);
	}
	
	//private static boolean needFix;
	
	public static final Properties DEFAULT = new Properties();
	public static SpireConfig config = null;
	
	private void initSavingConfig() {
		DEFAULT.setProperty(PortableAltar.SAVE_NAME, "0");
		DEFAULT.setProperty(Sins.SAVE_NAME, "0");
		DEFAULT.setProperty(TimeTraveler.SAVE_NAME, "100");
		// DEFAULT.setProperty("recorded", "false");
		DEFAULT.setProperty(DragonStarHat.SAVE_NAME, "0");
		DEFAULT.setProperty(Faith.SAVE_NAME, "false");
		DEFAULT.setProperty(Faith.SAVE_NAME1, "0");
		DEFAULT.setProperty(Mahjong.SAVE_KANG, "0");
		DEFAULT.setProperty(Mahjong.SAVE_TURN, "0");
		DEFAULT.setProperty(Mahjong.SAVE_REACH, "false");
		DEFAULT.setProperty(AscensionHeart.SAVE_NAME, "false");
		for (int i = 0; i < Mahjong.YAMA_NAME.length; i++)
			DEFAULT.setProperty(Mahjong.YAMA_NAME[i], "" + Mahjong.YAMA_DEFAULT[i]);
		for (String s : Mahjong.KANG_NAME)
			DEFAULT.setProperty(s, "-1");
		for (String s : Mahjong.DORA_NAME)
			DEFAULT.setProperty(s, "0");
		for (int i = 0; i < 13; i++)
			DEFAULT.setProperty(Mahjong.HAND_NAME + i, "0");
		try {
			config = new SpireConfig(SAVE_NAME, SAVE_FILE_NAME, DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void receiveStartGame() {
		if (config == null)
			this.initSavingConfig();

		SUB_MOD.forEach(TestMod::editSubModStartGame);
		
		if (AbstractDungeon.player.relics.stream().anyMatch(r -> r instanceof PortableAltar))
			PortableAltar.load(getInt(PortableAltar.SAVE_NAME));
		
		Sins.load(getInt(Sins.SAVE_NAME));
		if (Sins.checkModified()) {
			Sins.load(AbstractDungeon.player.maxHealth);
			save(Sins.SAVE_NAME, Sins.preMaxHP);
		}
		Sins.isSelect = false;
		
		HeartOfDaVinci.clear();
		HeartOfDaVinci.init(AbstractDungeon.player.relics.size());
		
		TimeTraveler.load(getInt(TimeTraveler.SAVE_NAME));
		
		Motorcycle.loadGame();
		// NoteOfAlchemist.setState(config.getBool("recorded"));
		
		DragonStarHat.loadValue(getInt(DragonStarHat.SAVE_NAME));
		
		Faith.load(getBool(Faith.SAVE_NAME), getInt(Faith.SAVE_NAME1));

		if (AbstractDungeon.player.hasRelic(makeID(Mahjong.ID))) {
			int[] yama = new int[37], kang = new int[getInt(Mahjong.SAVE_KANG)], hint = new int[kang.length + 1],
					hand = new int[13 - 3 * kang.length];
			for (int i = 0; i < 37; i++)
				yama[i] = getInt(Mahjong.YAMA_NAME[i]);
			for (int i = 0; i < kang.length; i++)
				kang[i] = getInt(Mahjong.KANG_NAME[i]);
			for (int i = 0; i < kang.length + 1; i++)
				hint[i] = getInt(Mahjong.DORA_NAME[i]);
			for (int i = 0; i < hand.length; i++)
				hand[i] = getInt(Mahjong.HAND_NAME + i);
			Mahjong.load(getInt(Mahjong.SAVE_TURN), getBool(Mahjong.SAVE_REACH), yama, kang, hint, hand);
		}

		Automaton.loadMagicNumber();
		
		AscensionHeart.load(getBool(AscensionHeart.SAVE_NAME));
		
		TemporaryBarricade.pulseLoader();
		
		IWantAll.loadVictory();
	}
	
	public static boolean hasSaveData(String key) {
		return config.has(key);
	}
	
	public static int getInt(String key) {
		return config.getInt(key);
	}
	
	public static boolean getBool(String key) {
		return config.getBool(key);
	}
	
	public static void save(String key, int value) {
		config.setInt(key, value);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(String key, boolean value) {
		config.setBool(key, value);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addEvents() {
		Stream.of(BoxForYourself.class, PlateOfNloth.class)
				.forEach(c -> BaseMod.addEvent(ReflectionHacks.getPrivateStatic(c, "ID"), c));
	}

	private static void addPotions() {
		Stream.of(EscapePotion.class, TimePotion.class, SpacePotion.class, TestPotion.class)
				.forEach(c -> BaseMod.addPotion(c, null, null, null, ReflectionHacks.getPrivateStatic(c, "POTION_ID")));
	}
	
	@Override
	public void receivePostInitialize() {
		RelicSelectScreen.initialize();
		avoidFirstTime();
		//needFix = !BaseMod.hasModID("ZeroMaxHealthFix:");
		
		/**
		 * TODO since version 3.3.9
		checkOldID();
		 * end version 4.2.0
		 */
		
		addEvents();
		addPotions();
		
		if ("280 chan".equals(CardCrawlGame.playerName))
			unlockAll();
		
		/*
		initCheat();
		*/
		initLatest();
		// TODO
		initializeModPanel();
		
		//enableConsoleMultiplayer();
	}
	
	private void initializeModPanel() {
		Texture badgeTexture = new Texture(powerIMGPath("relic1"));
		this.settingsPanel = new ModPanel();
		// ModLabeledButton filterRelics;
		/*if (Settings.language == GameLanguage.ZHS) {
			filterRelics = new ModLabeledButton("筛选遗物", 350.0F, 300.0F, this.settingsPanel, (button) -> {
				// new RelicFilterSelectScreen().open();
			});
		}*/
		// this.settingsPanel.addUIElement(filterRelics);
		if (Settings.language == GameLanguage.ZHS) {
			BaseMod.registerModBadge(badgeTexture, MOD_NAME, "彼君不触",
					"遗物、卡牌、事件、药水。", this.settingsPanel);
		} else {
			BaseMod.registerModBadge(badgeTexture, MOD_NAME, "280chan",
					"Fun relics, cards, events, potions.", this.settingsPanel);
		}
	}
	
	public static <T> T randomItem(ArrayList<T> list, Random rng) {
		return list.get(rng.random(list.size() - 1));
	}
	
	public static <T> T randomItem(ArrayList<T> list, java.util.Random rng) {
		return list.get(rng.nextInt(list.size()));
	}
	
	@Override
	public void receiveOnBattleStart(AbstractRoom r) {
		SUB_MOD.forEach(new RoomTrigger(r)::preTrigger);
		PerfectCombo.setRng();
		AbstractMahjongCard.setRng();
		Mahjong.setRng();
		VirtualReality.reset();
	}

	@Override
	public int receiveMapHPChange(int amount) {
		return AbstractDungeon.player.relics.stream().filter(r -> r instanceof AbstractTestRelic)
				.map(r -> ((AbstractTestRelic) r).maxHPChanger()).reduce(a -> a, Function::andThen).apply(amount * 1f)
				.intValue();
	}
	
	public static String eventIMGPath(String ID) {
		String path = "resources/images/" + ID + ".jpg";
		if (fileExist(path))
			return path;
		path = "resources/images/" + ID + ".png";
		return fileExist(path) ? path : eventIMGPath("relic1");
	}
	
	public static String relicIMGPath(String ID) {
		String path = "resources/images/relics/" + ID + ".png";
		return fileExist(path) ? path : relicIMGPath("relic1");
	}
	
	public static String cardIMGPath(String ID) {
		String path = "resources/images/cards/" + ID + ".png";
		return fileExist(path) ? path : cardIMGPath("relic1");
	}
	
	public static String powerIMGPath(String ID) {
		String path = "resources/images/powers/" + ID + ".png";
		return fileExist(path) ? path : powerIMGPath("relic1");
	}
	
	public static boolean fileExist(String path) {
		return Gdx.files.internal(path).exists();
	}
	
	public static boolean spireWithFriendLogger = true;
	
	private static void changeConsoleMultiplayer(boolean value) throws ClassNotFoundException {
		ReflectionHacks.setPrivateStatic(Class.forName("chronoMods.TogetherManager"), "debug", value);
		spireWithFriendLogger = !value;
	}
	
	private static boolean checkMySteamName(Object o, Class<?> c) {
		return "彼君不触".equals(ReflectionHacks.getPrivate(o, c, "userName"));
	}
	
	private static boolean checkSteamName(Object remotePlayer) {
		Class<?> c = remotePlayer.getClass();
		return checkMySteamName(remotePlayer,
				c.getCanonicalName().equals("chronoMods.network.steam.SteamPlayer") ? c.getSuperclass() : c);
	}
	
	private static void checkEnableConsoleMultiplayer() {
		if (Loader.isModLoaded("chronoMods")) {
			try {
				if ("280 chan".equals(CardCrawlGame.playerName)) {
					changeConsoleMultiplayer(true);
				} else {
					CopyOnWriteArrayList<?> players = ReflectionHacks
							.getPrivateStatic(Class.forName("chronoMods.TogetherManager"), "players");
					if (players.stream().anyMatch(TestMod::checkSteamName)) {
						changeConsoleMultiplayer(true);
						return;
					}
					changeConsoleMultiplayer(false);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void callPotionBeltPatch() {
		try {
			Class<?> c = Class.forName("chronoMods.coop.relics.VaporFunnel$PotionBeltPostAcquire");
			ReflectionHacks.privateStaticMethod(c, "Prefix", PotionBelt.class).invoke(new Object[] { null });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean addPotionSlotMultiplayer() {
		if (AbstractDungeon.player.hasBlight("VaporFunnel")) {
			info("拥有共享药水栏位荒疫，开始增加团队药水栏位");
			callPotionBeltPatch();
			return true;
		} else if (Loader.isModLoaded("chronoMods")) {
			info("未拥有共享药水栏位荒疫，跳过");
		}
		return false;
	}
	
	public static void addNyarlathotepPower(ArrayList<String> list) {
		Nyarlathotep.addPowerList(list);
	}

	public static void addNyarlathotepPower(String... list) {
		Nyarlathotep.addPowerList(list);
	}

	public static void addNyarlathotepRelic(ArrayList<String> list) {
		Nyarlathotep.addRelicList(list);
	}

	public static void addNyarlathotepRelic(String... list) {
		Nyarlathotep.addRelicList(list);
	}

	private static void exampleNyarlathotepAddRelic() {
		String instruction = "Copy this method to your mod, call it in receivePostInitialize() will register your relics that make effect when player plays a power card.";
		if (Loader.isModLoaded("testmod")) {
			ArrayList<String> list = new ArrayList<String>();
			list.add("relic id0");
			list.add("relic id1");
			try {
				ReflectionHacks.privateStaticMethod(Class.forName("mymod.TestMod"), "addNyarlathotepRelic",
						new Class[] { ArrayList.class }).invoke(null, new Object[] { list });
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
