package testmod.mymod;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.Settings.GameLanguage;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PotionBelt;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.BaseMod;
import basemod.DevConsole;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import christmasMod.mymod.ChristmasMod;
import halloweenMod.mymod.HalloweenMod;
import testmod.cards.*;
import testmod.cards.colorless.*;
import testmod.cards.mahjong.*;
import testmod.commands.*;
import testmod.events.*;
import testmod.patches.SwFPatch;
import testmod.potions.*;
import testmod.powers.*;
import testmod.relics.*;
import testmod.relicsup.AllUpgradeRelic;
import testmod.relicsup.AscensionHeartUp;
import testmod.relicsup.TestBoxUp;
import testmod.screens.RelicSelectScreen;
import testmod.utils.*;
import testmod.utils.GetRelicTrigger.RelicGetManager;

/**
 * @author 彼君不触
 * @version 5/30/2022
 * @since 6/17/2018
 */

@SpireInitializer
public class TestMod implements EditRelicsSubscriber, EditCardsSubscriber, EditStringsSubscriber, StartGameSubscriber,
		PreUpdateSubscriber, PostUpdateSubscriber, PostInitializeSubscriber, PostDungeonInitializeSubscriber,
		OnStartBattleSubscriber, MaxHPChangeSubscriber, EditKeywordsSubscriber, PostBattleSubscriber, MiscMethods,
		RelicGetSubscriber, PostCreateStartingRelicsSubscriber {
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
		}
	}
	
	private static void editSubModCards(ISubscriber sub) {
		if (sub instanceof EditCardsSubscriber) {
			((EditCardsSubscriber)sub).receiveEditCards();
		}
	}
	
	private static void editSubModKeywords(ISubscriber sub) {
		if (sub instanceof EditKeywordsSubscriber) {
			((EditKeywordsSubscriber)sub).receiveEditKeywords();
		}
	}
	
	private static void editSubModStrings(ISubscriber sub) {
		if (sub instanceof EditStringsSubscriber) {
			((EditStringsSubscriber)sub).receiveEditStrings();
		}
	}
	
	private static void editSubModPostDungeonInit(ISubscriber sub) {
		if (sub instanceof PostDungeonInitializeSubscriber) {
			((PostDungeonInitializeSubscriber)sub).receivePostDungeonInitialize();
		}
	}
	
	private static void editSubModPostUpdate(ISubscriber sub) {
		if (sub instanceof PostUpdateSubscriber) {
			((PostUpdateSubscriber)sub).receivePostUpdate();
		}
	}
	
	private static void editSubModStartGame(ISubscriber sub) {
		if (sub instanceof StartGameSubscriber) {
			((StartGameSubscriber)sub).receiveStartGame();
		}
	}
	
	private static void editSubModStartBattle(ISubscriber sub, AbstractRoom r) {
		if (sub instanceof OnStartBattleSubscriber) {
			((OnStartBattleSubscriber)sub).receiveOnBattleStart(r);
		}
	}
	
	private static void editSubModPostBattle(ISubscriber sub, AbstractRoom r) {
		if (sub instanceof PostBattleSubscriber) {
			((PostBattleSubscriber)sub).receivePostBattle(r);
		}
	}

	@Override
	public void receiveEditKeywords() {
		// TODO
		Stream.of(new Gson().fromJson(readString("keywords"), Keyword[].class))
				.forEach(k -> BaseMod.addKeyword(k.PROPER_NAME, k.NAMES, k.DESCRIPTION));
		//BaseMod.addKeyword(arg0, arg1, arg2, arg3);
		
		switch (Settings.language) {
		case ZHS:
		case ZHT:
			BaseMod.addKeyword(new String[] { "生气" },
					AngryPower.DESCRIPTIONS[1] + 1 + AngryPower.DESCRIPTIONS[2]);
			break;
		default:
			BaseMod.addKeyword(new String[] { "Angry" },
					AngryPower.DESCRIPTIONS[1] + 1 + AngryPower.DESCRIPTIONS[2]);
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
	
	public static void info(Object s) {
		LOGGER.info(s);
	}
	
	public static boolean isLocalTest() {
		return checkHash(CardCrawlGame.playerName, "1023dba2e158f257fba87f85d932b1df69c1989dc87c14389787a681f056cc5e")
				&& checkHash(TestMod.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
						"77ce59ba93d1f3e3087656846b13cf4b197f693cc21cf2aeaf1197dbf6c2c4a6");
	}
	
	private static final HashMap<String, String> MAKE_ID_MAP = new HashMap<String, String>();
	
	public static String makeID(String id) {
		if (MAKE_ID_MAP.containsKey(id))
			return MAKE_ID_MAP.get(id);
		if (id.length() > TestMod.MOD_ID.length() + 1 && id.substring(0, TestMod.MOD_ID.length()).equals(MOD_ID)) {
			info("这个为什么id重复了前缀:" + id);
			return id;
		}
		MAKE_ID_MAP.put(id, MOD_ID + "-" + id);
		return MAKE_ID_MAP.get(id);
	}
	
	public static String unMakeID(String newID) {
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
		// MAHJONGS.stream().map(c -> c.cardID).forEach(TestMod::checkOldCardID);
	}
	
	private static void checkOldRelicID(String id) {
		if (UnlockTracker.isRelicSeen(unMakeID(id)))
			UnlockTracker.markRelicAsSeen(id);
	}
	
	private static void checkOldCardID(String id) {
		if (UnlockTracker.isCardSeen(unMakeID(id)))
			UnlockTracker.markCardAsSeen(id);
	}
	
	
	public static String lanPrefix(String s) {
		// TODO
		String tmp = Settings.language.name().toLowerCase() + "/" + s;
		if (fileExist(stringsPathFix(tmp)))
			return tmp;
		return (Settings.language == GameLanguage.ZHT ? "zhs/" : "eng/") + s;
	}
	
	public static String stringsPathFix(String s) {
		return "testmodResources/strings/" + s + ".json";
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
		addLatest(new GremlinBalance(), new GoldenSoul(), new GreedyDevil(),
				new TemporaryBarricade(), new StomachOfGluttonous(), new PhasePocketWatch());
		BAD_RELICS = MY_RELICS.stream().filter(AbstractTestRelic::isBad).collect(toArrayList());
		addLatest(new Enchant(), new VirtualReality(), new WeaknessCounterattack(), new Reproduce(),
				new HandmadeProducts(), new Automaton());
	}

	private static void addRelic(AbstractRelic r) {
		BaseMod.addRelic(r, RelicType.SHARED);
	}
	
	@Override
	public void receiveEditRelics() {
		// 添加遗物进游戏
		// new Mahjong();
		RELICS = Stream.of(new ObsoleteBoomerang(), new BlackFramedGlasses(), new BouquetWithThorns(), new IWantAll(),
				new PortableAltar(), new Sins(), new Register(), new EnergyCheck(), new TestBox(), new CasingShield(),
				new OneHitWonder(), new RandomTest(), new Temperance(), new Justice(), new Fortitude(), new Charity(),
				new GoldenSoul(), new Dye(), new AssaultLearning(), new HeartOfDaVinci(), new IncinerationGenerator(),
				new IndustrialRevolution(), new AscensionHeart(), new RealStoneCalender(), new Prudence(), new Hope(),
				new Maize(), new DragonStarHat(), new SaigiyounoYou(), new Motorcycle(), new DreamHouse(), new Nine(),
				new BatchProcessingSystem(), new CardMagician(), new NegativeEmotionEnhancer(), new InfectionSource(),
				new FatalChain(), new VoidShard(), new CyclicPeriapt(), new EqualTreatment(), new ConstraintPeriapt(),
				new BloodSacrificeSpiritualization(), new Acrobat(), new ArcanaOfDestiny(), new TurbochargingSystem(),
				new GremlinBalance(), new Restrained(), new Faith(), new HyperplasticTissue(), new TraineeEconomist(),
				new InjuryResistance(), new DeterminationOfClimber(), new Déjàvu(), new Muramasa(), new GreedyDevil(),
				new RainbowHikingShoes(), new DominatorOfWeakness(), new D_4(), new ShadowAmulet(), new RetroFilter(),
				new CrystalShield(), new Alchemist(), new StringDisintegrator(), new HarvestTotem(), new Fanaticism(),
				new Antiphasic(), new IntensifyImprint(), new KeyOfTheVoid(), new ThousandKnives(), new GiantKiller(),
				new PhasePocketWatch(), new MagicalMallet(), new EvilDagger(), new TimeTraveler(), new Nyarlathotep(),
				new VentureCapital(), new LifeArmor(), new HeartOfStrike(), new Iteration(), new TemporaryBarricade(),
				new TheFather(), new TwinklingStar(), new GlassSoul(), new Laevatain(), new Match3(), new Brilliant(), 
				new BalancedPeriapt(), new ConjureBlade(), new PortablePortal(), new Encyclopedia(), new SpireNexus(),
				new Gather(), new FissionDevice(), new DemonSummon(), new HolyLightProtection(), new GoldenContract(),
				new StomachOfGluttonous(), new AbnormalityKiller(), new Metronome(), new ResonanceStone(),
				new Reverse(), new Extravagant())
				.collect(toArrayList());
		if (!Loader.isModLoaded("FoggyMod"))
			RELICS.add(new MistCore());
		SUB_MOD.forEach(TestMod::editSubModRelics);
		RELICS.forEach(TestMod::addRelic);
		MY_RELICS = RELICS.stream().filter(r -> r instanceof AbstractTestRelic).map(r -> (AbstractTestRelic) r)
				.collect(toArrayList());
		
		MY_RELICS.forEach(AbstractTestRelic::addToMap);
		if (Loader.isModLoaded("RelicUpgradeLib"))
			MY_RELICS.forEach(AllUpgradeRelic::add);
	}

	public static ArrayList<AbstractRelic> RELICS = new ArrayList<AbstractRelic>();
	public static ArrayList<AbstractTestRelic> MY_RELICS = new ArrayList<AbstractTestRelic>();
	
	private void loadStrings(Class<?> c) {
		String s = c.getSimpleName().toLowerCase();
		BaseMod.loadCustomStrings(c, readString(s.substring(0, s.length() - 6)));
	}
	
	private void loadUpgradedRelicStrings() {
		String s = "upgraded" + RelicStrings.class.getSimpleName().toLowerCase();
		BaseMod.loadCustomStrings(RelicStrings.class, readString(s.substring(0, s.length() - 6)));
	}
	
	private void loadUpgradedRelicCosts() {
		BaseMod.loadCustomStrings(UIStrings.class,
				Gdx.files.internal(stringsPathFix("upgrade")).readString(String.valueOf(StandardCharsets.UTF_8)));
	}
	
	@Override
	public void receiveEditStrings() {
		Stream.of(RelicStrings.class, CardStrings.class, PowerStrings.class, PotionStrings.class, EventStrings.class,
				UIStrings.class).forEach(this::loadStrings);
		this.loadUpgradedRelicCosts();
		this.loadUpgradedRelicStrings();
		SUB_MOD.forEach(TestMod::editSubModStrings);
	}

	public static void add(AbstractCard c) {
		CARDS.add(c);
	}
	
	@Override
	public void receiveEditCards() {
		Stream.of(Sins.SINS).filter(c -> c instanceof AbstractTestCard).forEach(BaseMod::addCard);
		// 添加卡牌进游戏
		CARDS = Stream.of(new LifeRuler(), new WeaknessCounterattack(), new SubstituteBySubterfuge(),
				new Superconductor(), new EternalityOfKhronos(), new AutoReboundSystem(), new Wormhole(),
				new RepeatForm(), new SunMoon(), new Mystery(), new ShutDown(), new Reflect(),
				new PocketStoneCalender(), new Recap(), new Bloodthirsty(), new Arrangement(), new BloodShelter(),
				new SelfRegulatingSystem(), new Automaton(), new LimitFlipper(), new TemporaryDeletion(),
				new RabbitOfFibonacci(), new TradeIn(), new Reproduce(), new PainDetonator(), new Reverberation(),
				new CardIndex(), new PowerStrike(), new TaurusBlackCat(), new PerfectCombo(), new Lexicography(),
				new Librarian(), new VirtualReality(), new HandmadeProducts(), new DeathImprint(),
				new TreasureHunter(), new MoneyShot(), new Enchant(), new FormForm(), new PortableTranscript())
				.collect(toArrayList());
		new AddAnonymousCards().add();
		
		
		CARDS.forEach(BaseMod::addCard);
		SUB_MOD.forEach(TestMod::editSubModCards);
		
		// this.getNaturalNumberList(37).stream().map(AbstractMahjongCard::mahjong).forEach(this::addMahjongToList);
		
		
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
		LATEST = Stream.of(list).collect(toArrayList());
	}
	
	private void addLatest(AbstractCard... list) {
		LATEST_CARD = Stream.of(list).collect(toArrayList());
	}

	@Override
	public void receivePostCreateStartingRelics(PlayerClass c, ArrayList<String> relics) {
		relics.add(TestMod.makeID("TestBox"));
	}
	
	@Override
	public void receivePostDungeonInitialize() {
		if (AbstractDungeon.floorNum < 2) {
			checkEnableConsoleMultiplayer();
			
			if (config == null)
				this.initSavingConfig();
			SUB_MOD.forEach(TestMod::editSubModPostDungeonInit);
			TheFatherPower.clear();
			DragonStarHat.resetValue();
			Faith.reset();
			AscensionHeart.reset();
			AscensionHeartUp.reset();
			// Mahjong.saveDefaultYama();
			
			if (this.relicStream(TestBox.class).count() < 1) {
				obtain(p(), new TestBox());
				TestMod.info("礼物盒小于1，添加");
			} else if (Stream.of("c870968e2499df3ec4a1e386c21f19628af4cef6e5aaa8aa6da2071ab1fba5e4",
							"a4e624d686e03ed2767c0abd85c14426b0b1157d2ce81d27bb4fe4f6f01d688a",
							"342840f6340d15691f4be1c0e0157fb0983992c4f436c18267d41dbe6bb74a2")
					.anyMatch(s -> TestMod.checkHash(CardCrawlGame.playerName, s))) {
				TestBoxUp r = new TestBoxUp();
				p().relics.set(p().relics.indexOf(this.relicStream(TestBox.class).findFirst().get()), r);
				p().reorganizeRelics();
				r.onEquip();
			}
			ManifoldPotion.clear();
		}
	}
	
	public void unlockAll() {
		info("开始解锁");
		CARDS.forEach(this::markAsSeen);
		Stream.of(Sins.SINS).filter(c -> c instanceof AbstractTestCard).forEach(this::markAsSeen);
		// MAHJONGS.forEach(this::markAsSeen);
		RELICS.forEach(this::markAsSeen);
	}
	
	private static final ArrayList<AbstractRelic> TO_OBTAIN = new ArrayList<AbstractRelic>();
	
	public static void obtainLater(AbstractRelic r) {
		TO_OBTAIN.add(r);
	}
	
	public AbstractRelic randomBonusRelic() {
		ArrayList<AbstractRelic> unSeen = RELICS.stream().filter(r -> !r.isSeen).collect(toArrayList());
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
		ArrayList<AbstractCard> unSeen = CARDS.stream().filter(c -> !c.isSeen).collect(toArrayList());
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
		if (AbstractDungeon.player != null) {
			this.relicStream().forEach(AbstractTestRelic::preUpdate);
			this.updateGlow();
		}
	}
	
	private static void invoke(AbstractTestRelic r, boolean equip) {
		try {
			r.getClass().getMethod(equip ? "equipAction" : "unequipAction").invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
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
		
		if (p() != null) {
			if (!TO_OBTAIN.isEmpty() && AbstractDungeon.topLevelEffects.isEmpty()) {
				obtain(p(), TO_OBTAIN.remove(0), true);
			}

			this.relicStream().peek(TestMod::setActivity).forEach(TestMod::setShow);
			MY_RELICS.stream().filter(AbstractTestRelic::tryEquip).forEach(this::invokeEquip);
			MY_RELICS.stream().filter(AbstractTestRelic::tryUnequip).forEach(this::invokeUnequip);
			this.relicStream().forEach(AbstractTestRelic::postUpdate);
			
			if (AbstractDungeon.currMapNode != null) {
				if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
					PerfectCombo.TO_UPDATE.stream().filter(p().hand::contains)
							.filter(c -> c.magicNumber < c.countUpgrades() + c.misc).forEach(c -> c.applyPowers());
					Stream<AbstractUpdatableCard> s = AbstractUpdatableCard.TO_UPDATE.stream()
							.filter(p().hand::contains)
							.peek(c -> c.preApplyPowers(p(), AbstractDungeon.getMonsters().hoveredMonster));
					if (!this.hasStringDisintegrator())
						s.forEach(c -> c.applyPowers());
					else
						s.close();
					AbstractUpdatableCard.TO_UPDATE.stream().filter(not(p().hand::contains))
							.forEach(c -> c.resetDescription());
				} else {
					PerfectCombo.TO_UPDATE.clear();
					AbstractUpdatableCard.TO_UPDATE.clear();
				}
			}

			this.turnSkipperUpdate();
			
			BoxForYourself.updateThis();
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
		DEFAULT.setProperty(AscensionHeart.SAVE_SIZE, "0");
		DEFAULT.setProperty(AscensionHeartUp.SAVE_SIZE, "0");
		DEFAULT.setProperty(GlassSoul.ID, "0");
		DEFAULT.setProperty(Metronome.ID, "0");
		DEFAULT.setProperty(PhasePocketWatch.SAVE_NAME, "0");
		DEFAULT.setProperty(Encyclopedia.SAVE_NAME, "0");
		DEFAULT.setProperty(ManifoldPotion.POTION_ID, "");

		if (Loader.isModLoaded("RelicUpgradeLib")) {
			Stream.of(AllUpgradeRelic.MultiKey.SAVE_NAME).forEach(s -> DEFAULT.setProperty(s, "0"));
		}
		
		/*DEFAULT.setProperty(Mahjong.SAVE_KANG, "0");
		DEFAULT.setProperty(Mahjong.SAVE_TURN, "0");
		DEFAULT.setProperty(Mahjong.SAVE_REACH, "false");
		for (int i = 0; i < Mahjong.YAMA_NAME.length; i++)
			DEFAULT.setProperty(Mahjong.YAMA_NAME[i], "" + Mahjong.YAMA_DEFAULT[i]);
		for (String s : Mahjong.KANG_NAME)
			DEFAULT.setProperty(s, "-1");
		for (String s : Mahjong.DORA_NAME)
			DEFAULT.setProperty(s, "0");
		for (int i = 0; i < 13; i++)
			DEFAULT.setProperty(Mahjong.HAND_NAME + i, "0");*/
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

		if (Loader.isModLoaded("RelicUpgradeLib")) {
			if (CardCrawlGame.loadingSave) {
				AllUpgradeRelic.MultiKey.load();
			} else {
				AllUpgradeRelic.MultiKey.reset();
			}
		}
		
		if (p().relics.stream().anyMatch(r -> r instanceof PortableAltar))
			PortableAltar.load(getInt(PortableAltar.SAVE_NAME));
		
		Sins.load(getInt(Sins.SAVE_NAME));
		if (Sins.checkModified()) {
			Sins.load(AbstractDungeon.player.maxHealth);
			save(Sins.SAVE_NAME, Sins.preMaxHP);
		}
		Sins.isSelect = false;
		
		HeartOfDaVinci.clear();
		
		TimeTraveler.load(getInt(TimeTraveler.SAVE_NAME));
		Motorcycle.loadGame();
		DragonStarHat.loadValue(getInt(DragonStarHat.SAVE_NAME));
		Faith.load(getBool(Faith.SAVE_NAME), getInt(Faith.SAVE_NAME1));

		/*if (AbstractDungeon.player.hasRelic(makeID(Mahjong.ID))) {
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
		}*/

		Automaton.loadMagicNumber();
		AscensionHeart.load(getInt(AscensionHeart.SAVE_SIZE));
		AscensionHeartUp.load(getInt(AscensionHeartUp.SAVE_SIZE));
		TemporaryBarricade.pulseLoader();
		IWantAll.loadVictory();
		GlassSoul.load(getStringList(GlassSoul.ID));
		Metronome.load();
		PhasePocketWatch.load(getInt(PhasePocketWatch.SAVE_NAME));
		Encyclopedia.load();
		ManifoldPotion.load();
	}
	
	public static boolean hasSaveData(String key) {
		return config.has(key);
	}
	
	public static String getString(String key) {
		return config.getString(key);
	}
	
	public static int getInt(String key) {
		return config.getInt(key);
	}
	
	public static boolean getBool(String key) {
		return config.getBool(key);
	}
	
	public static ArrayList<Integer> getIntList(String key) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int size = config.getInt(key);
		for (int i = 0; i < size; i++)
			list.add(config.getInt(key + i));
		return list;
	}
	
	public static ArrayList<Boolean> getBooleanList(String key) {
		ArrayList<Boolean> list = new ArrayList<Boolean>();
		int size = config.getInt(key);
		for (int i = 0; i < size; i++)
			list.add(config.getBool(key + i));
		return list;
	}
	
	public static ArrayList<String> getStringList(String key) {
		ArrayList<String> list = new ArrayList<String>();
		int size = config.getInt(key);
		for (int i = 0; i < size; i++)
			list.add(config.getString(key + i));
		return list;
	}
	
	public static void save() {
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveInt(String key, ArrayList<Integer> list) {
		config.setInt(key, list.size());
		for (int i = 0; i < list.size(); i++)
			config.setInt(key + i, list.get(i));
		save();
	}
	
	public static void saveBoolean(String key, ArrayList<Boolean> list) {
		config.setInt(key, list.size());
		for (int i = 0; i < list.size(); i++)
			config.setBool(key + i, list.get(i));
		save();
	}
	
	public static void saveString(String key, ArrayList<String> list) {
		config.setInt(key, list.size());
		for (int i = 0; i < list.size(); i++)
			config.setString(key + i, list.get(i));
		save();
	}
	
	public static void save(String key, int value) {
		config.setInt(key, value);
		save();
	}
	
	public static void save(String key, boolean value) {
		config.setBool(key, value);
		save();
	}
	
	public static void save(String key, String value) {
		config.setString(key, value);
		save();
	}

	private static void addEvents() {
		Stream.of(BoxForYourself.class, PlateOfNloth.class, MysteryExchangeTable.class)
				.forEach(AbstractTestEvent::addEvent);
	}

	private static void addPotions() {
		Stream.of(EscapePotion.class, TimePotion.class, SpacePotion.class, TestPotion.class, ManifoldPotion.class)
				.forEach(c -> BaseMod.addPotion(c, null, null, null, ReflectionHacks.getPrivateStatic(c, "POTION_ID")));
	}
	
	@Override
	public void receivePostInitialize() {
		RelicSelectScreen.initialize();
		avoidFirstTime();
		//needFix = !BaseMod.hasModID("ZeroMaxHealthFix:");
		
		/**
		 * since version 3.3.9
		checkOldID();
		 * end version 4.2.0
		 */
		
		addEvents();
		addPotions();
		
		if (checkHash(CardCrawlGame.playerName, "1023dba2e158f257fba87f85d932b1df69c1989dc87c14389787a681f056cc5e")) {
			unlockAll();
			TestCommand.add("test", Test.class);
		}
		TestCommand.add("relictest", Relic.class);
		TestCommand.add("handsize", HandSize.class);
		TestCommand.add("ascension", Ascension.class);
		
		initLatest();
		
		// TODO
		initializeModPanel();
		
		if (Loader.isModLoaded("chronoMods")) {
			DevConsole.enabled = true;
			SwFPatch.StupidSwFPatch.init = false;
		}
		
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
		if (Settings.language == GameLanguage.ZHS || Settings.language == GameLanguage.ZHT) {
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
		// Mahjong.setRng();
		VirtualReality.reset();
	}

	@Override
	public int receiveMapHPChange(int amount) {
		return chain(relicStream().map(r -> r.maxHPChanger())).apply(amount * 1f).intValue();
	}

	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (!RelicGetManager.loading) {
			this.relicStream().filter(a -> a instanceof GetRelicTrigger)
					.forEach(a -> ((GetRelicTrigger) a).receiveRelicGet(r));
		}
	}
	
	private static final String IMG_Prefix = "testmodResources/images/";
	
	private static String imgPath(String ID, String type, String postfix) {
		String path = IMG_Prefix + type + ID + ".png";
		if (fileExist(path))
			return path;
		if (postfix != null)
			path = IMG_Prefix + type + ID + postfix;
		return fileExist(path) ? path : imgPath("relic1", type, postfix);
	}
	
	public static String eventIMGPath(String ID) {
		return imgPath(ID, "", ".jpg");
	}
	
	public static String relicIMGPath(String ID) {
		return imgPath(ID, "relics/", null);
	}
	
	public static String cardIMGPath(String ID) {
		return imgPath(ID, "cards/", null);
	}
	
	public static String powerIMGPath(String ID) {
		return imgPath(ID, "powers/", null);
	}
	
	public static boolean fileExist(String path) {
		return Gdx.files.internal(path).exists();
	}
	
	public static boolean spireWithFriendLogger = true;
	
	public static String hash(String input) {
		try {
			return new BigInteger(1, MessageDigest.getInstance("SHA-256").digest((input + input.length()).getBytes()))
					.toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return input;
	}
	
	public static boolean checkHash(String input, String hash) {
		return hash.equals(hash(input));
	}
	
	private static void changeConsoleMultiplayer(boolean value) throws ClassNotFoundException {
		ReflectionHacks.setPrivateStaticFinal(Class.forName("chronoMods.TogetherManager"), "debug", value);
		DevConsole.enabled |= value;
		spireWithFriendLogger = !value;
	}
	
	private static boolean checkMySteamName(Object o, Class<?> c) {
		return checkHash(ReflectionHacks.getPrivate(o, c, "userName"),
				"520fbb3f83c083f3c6ad52f5943fd5e24b4b042d7911b0ed229aea9b264f664");
	}
	
	private static boolean checkSteamName(Object remotePlayer) {
		Class<?> c = remotePlayer.getClass();
		return checkMySteamName(remotePlayer,
				c.getCanonicalName().equals("chronoMods.network.steam.SteamPlayer") ? c.getSuperclass() : c);
	}
	
	private static void checkEnableConsoleMultiplayer() {
		if (Loader.isModLoaded("chronoMods")) {
			boolean enableConsole = true;
			try {
				if (enableConsole || checkHash(CardCrawlGame.playerName,
						"1023dba2e158f257fba87f85d932b1df69c1989dc87c14389787a681f056cc5e")) {
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
	
	public static void addNyarlathotepCard(ArrayList<String> list) {
		Nyarlathotep.addCardList(list);
	}

	public static void addNyarlathotepCard(String... list) {
		Nyarlathotep.addCardList(list);
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
		String instruction = "Copy this method to your mod, call it in receivePostInitialize() will register your "
				+ "relics that make effect when player plays a power card.";
		if (Loader.isModLoaded("testmod")) {
			ArrayList<String> list = new ArrayList<String>();
			list.add("relic id0");
			list.add("relic id1");
			try {
				ReflectionHacks.privateStaticMethod(Class.forName("testmod.mymod.TestMod"), "addNyarlathotepRelic",
						new Class[] { ArrayList.class }).invoke(null, new Object[] { list });
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
