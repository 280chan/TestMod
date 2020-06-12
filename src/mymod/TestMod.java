package mymod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.codedisaster.steamworks.SteamAPI;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.Settings.GameLanguage;
import com.megacrit.cardcrawl.core.CardCrawlGame.GameMode;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import actions.PerfectComboAction;
import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.PreUpdateSubscriber;
import basemod.interfaces.StartGameSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.MaxHPChangeSubscriber;
import cards.*;
import cards.colorless.*;
import cards.mahjong.*;
import deprecated.relics.*;
import events.*;
import potions.*;
import powers.SuperconductorNoEnergyPower;
import relics.*;
import utils.*;

/**
 * @author 彼君不触
 * @version 6/12/2020
 * @since 6/17/2018
 */

@SpireInitializer
public class TestMod implements EditRelicsSubscriber, EditCardsSubscriber, EditStringsSubscriber, PostDungeonInitializeSubscriber, PreUpdateSubscriber, PostUpdateSubscriber, StartGameSubscriber, PostInitializeSubscriber, OnStartBattleSubscriber, MaxHPChangeSubscriber, MiscMethods {
	public static Mode MODE = Mode.BOX;
	public static final String MOD_ID = "testmod";
	public static final Logger LOGGER = LogManager.getLogger(TestMod.class.getName());

	public static enum Mode {
		TEST, CHEAT, RANDOM, RELIC, CARD, BOTH, BOX;
		private Mode() {
		}
		public boolean test() {
			return this == TEST;
		}
		public boolean cheat() {
			return this == CHEAT;
		}
		public boolean random() {
			return this == RANDOM;
		}
		public boolean relic() {
			return this == RELIC;
		}
		public boolean card() {
			return this == CARD;
		}
		public boolean both() {
			return this == BOTH;
		}
		public boolean box() {
			return this == BOX;
		}
	}
	
	public static void info(String s) {
		LOGGER.info(s);
	}
	
	public static boolean isLocalTest() {
		return CardCrawlGame.playerName.equals("280 chan") && !SteamAPI.isSteamRunning();
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
		for (AbstractRelic r : RELICS)
			checkOldRelicID(r.relicId);
		for (AbstractCard c : CARDS)
			checkOldCardID(c.cardID);
		if (UnlockTracker.isCardSeen("Collecter"))
			UnlockTracker.markCardAsSeen(makeID("Collector"));
		if (UnlockTracker.isCardSeen("LackOfEnergy"))
			UnlockTracker.markCardAsSeen(makeID("PocketStoneCalender"));
		for (AbstractCard c : Sins.SINS)
			if (!c.cardID.equals(Pride.ID))
				checkOldCardID(c.cardID);
		for (AbstractCard c : MAHJONGS)
			checkOldCardID(c.cardID);
	}
	
	private static void checkOldRelicID(String id) {
		if (UnlockTracker.isRelicSeen(unMakeID(id)))
			UnlockTracker.markRelicAsSeen(id);
	}
	
	private static void checkOldCardID(String id) {
		if(UnlockTracker.isCardSeen(unMakeID(id)))
			UnlockTracker.markCardAsSeen(id);
	}
	
	private static String langPrefix = null;
	
	public static String languagePrefix(String s) {
		langPrefix = "zhs/";
		// TODO
		if (Settings.language != GameLanguage.ZHS)
			return "eng/" + s;
			//langPrefix = Settings.language.name().toLowerCase() + "/";
		return langPrefix + s;
	}
	
	public static String stringsPathFix(String s) {
		return "resources/strings/" + s + ".json";
	}
	
	public static String readString(String type) {
		return Gdx.files.internal(stringsPathFix(languagePrefix(type))).readString(String.valueOf(StandardCharsets.UTF_8));
	}
	
	/**
	 * Entrance
	 */
	public static void initialize() {
		BaseMod.subscribe(new TestMod());
	}

	@Override
	public void receiveEditRelics() {
		AbstractRelic[] relic = { new ObsoleteBoomerang(), new D_4(), new BlackFramedGlasses(), new BouquetWithThorns(),
				new PortableAltar(), new Sins(), new Register(), new EnergyCheck(), new InfectionSource(),
				new OneHitWonder(), new Iteration(), new Temperance(), new Justice(), new Fortitude(), new Charity(),
				new Hope(), new AssaultLearning(), new Muramasa(), new HeartOfDaVinci(), new IncinerationGenerator(),
				new IndustrialRevolution(), new AscensionHeart(), new RealStoneCalender(), new Nyarlathotep(),
				new BalancedPeriapt(), new MagicalMallet(), new Laevatain(), new TimeTraveler(), new Prudence(),
				new Maize(), new DragonStarHat(), new Nine(), new SaigiyounoYou(), new Motorcycle(), new DreamHouse(),
				new CrystalShield(), new Alchemist(), new StringDisintegrator(), new HarvestTotem(), new LifeArmor(),
				new BatchProcessingSystem(), new CardMagician(), new NegativeEmotionEnhancer(), new Brilliant(),
				new IWantAll(), new Antiphasic(), new IntensifyImprint(), new KeyOfTheVoid(), new ThousandKnives(),
				new Faith(), new FatalChain(), new CyclicPeriapt(), new EqualTreatment(), new ConstraintPeriapt(),
				new InjuryResistance(), new DeterminationOfClimber(), new Déjàvu(), new CasingShield(), new TestBox(),
				new BloodSacrificeSpiritualization(), new Acrobat(), new Mahjong()
				};
		// 添加遗物进游戏 TODO
		for (AbstractRelic r : relic) {
			RELICS.add(r);
		}
		
		for (AbstractRelic r : RELICS) {
			BaseMod.addRelic(r, RelicType.SHARED);
			if (r instanceof MyRelic) {
				MY_RELICS.add((MyRelic)r);
			}
			if (r instanceof AbstractBottleRelic) {
				BOTTLES.add((AbstractBottleRelic)r);
			}
		}
		
		for (MyRelic r : MY_RELICS) {
			MyRelic.addToMap(r);
		}
		
		for (int i = 0; i < BOTTLES.size(); i++) {
			BaseMod.registerBottleRelic(BOTTLES.get(i), BOTTLES.get(i));
		}
		
	}

	public static final ArrayList<AbstractRelic> RELICS = new ArrayList<AbstractRelic>();
	private static final ArrayList<MyRelic> MY_RELICS = new ArrayList<MyRelic>();
	private static final ArrayList<AbstractBottleRelic> BOTTLES = new ArrayList<AbstractBottleRelic>();
	
	@Override
	public void receiveEditStrings() {
		BaseMod.loadCustomStrings(RelicStrings.class, readString("relics"));
		BaseMod.loadCustomStrings(CardStrings.class, readString("cards"));
	}

	@Override
	public void receiveEditCards() {
		for (AbstractCard c : Sins.SINS) {
			BaseMod.addCard(c);
		}
		
		AbstractCard[] card = { new DisillusionmentEcho(), new TreasureHunter(), new SubstituteBySubterfuge(),
				new PerfectCombo(), new PulseDistributor(), new LifeRuler(), new EternalityOfKhronos(), new Wormhole(),
				new AutoReboundSystem(), new ComboMaster(), new Collector(), new RepeatForm(), new BloodBlade(),
				new ShutDown(), new Provocation(), new PocketStoneCalender(), new Mystery(), new Bloodthirsty(),
				new BloodShelter(), new Reflect(), new Dream(), new ChaoticCore(), new LimitFlipper(),
				new ConditionedReflex(), new RabbitOfFibonacci(), new CardIndex(), new DeathImprint(),
				new Arrangement(), new AssimilatedRune(), new AdversityCounterattack(), new Recap(), new HeadAttack(),
				new TemporaryDeletion(), new EnhanceArmerment(), new TradeIn(), new DorothysBlackCat(),
				new PainDetonator(), new FightingIntention(), new Reverberation(), new SelfRegulatingSystem(),
				new Superconductor(), new BackupPower(), new Illusory(), new Librarian(), new HandmadeProducts(),
				new Automaton() };
		// TODO
		for (AbstractCard c : card) {
			CARDS.add(c);
		}
		
		for (int i = 0; i < 37; i++) {
			this.addMahjongToList(AbstractMahjongCard.mahjong(i));
		}
		
		//BaseMod.addCard(new Test());
		
		for (AbstractCard c : CARDS) {
			BaseMod.addCard(c);
		}
	}
	
	private void addMahjongToList(AbstractMahjongCard c) {
		BaseMod.addCard(c);
		MAHJONGS.add(c);
	}

	public static final ArrayList<AbstractMahjongCard> MAHJONGS = new ArrayList<AbstractMahjongCard>();
	public static final ArrayList<AbstractCard> CARDS = new ArrayList<AbstractCard>();
	
	public static void removeFromPool(AbstractRelic r) {
		switch (r.tier) {
		case COMMON:
			for (Iterator<String> s = AbstractDungeon.commonRelicPool.iterator(); s.hasNext();) {
				String derp = (String) s.next();
				if (derp.equals(r.relicId)) {
					s.remove();
					return;
				}
			}
		case UNCOMMON:
			for (Iterator<String> s = AbstractDungeon.uncommonRelicPool.iterator(); s.hasNext();) {
				String derp = (String) s.next();
				if (derp.equals(r.relicId)) {
					s.remove();
					return;
				}
			}
		case RARE:
			for (Iterator<String> s = AbstractDungeon.rareRelicPool.iterator(); s.hasNext();) {
				String derp = (String) s.next();
				if (derp.equals(r.relicId)) {
					s.remove();
					return;
				}
			}
		case BOSS:
			for (Iterator<String> s = AbstractDungeon.bossRelicPool.iterator(); s.hasNext();) {
				String derp = (String) s.next();
				if (derp.equals(r.relicId)) {
					s.remove();
					return;
				}
			}
		case SHOP:
			for (Iterator<String> s = AbstractDungeon.shopRelicPool.iterator(); s.hasNext();) {
				String derp = (String) s.next();
				if (derp.equals(r.relicId)) {
					s.remove();
					return;
				}
			}
		default:
		}
	}
	
	private static void avoidFirstTime() {
		if (TipTracker.pref != null)
			TipTracker.neverShowAgain("RELIC_TIP");
	}
	
	private static final ArrayList<Object> LATEST = new ArrayList<Object>();
	private static final ArrayList<ArrayList<Object>> INIT0 = new ArrayList<ArrayList<Object>>();
	private static final ArrayList<ArrayList<Object>> INIT = new ArrayList<ArrayList<Object>>();
	private static final ArrayList<ArrayList<Object>> INIT_R = new ArrayList<ArrayList<Object>>();
	private static final ArrayList<ArrayList<Object>> INIT_G = new ArrayList<ArrayList<Object>>();
	private static final ArrayList<ArrayList<Object>> INIT_B = new ArrayList<ArrayList<Object>>();
	
	private static int latestIndex = 0;
	
	private static boolean checkLatest(AbstractPlayer p) {
		if (CardCrawlGame.playerName.equals("BrkStarshine")) {
			info("Checked");
			if (latestIndex < LATEST.size()) {
				Object o = LATEST.get(latestIndex++);
				obtain(p, o);
				return true;
			}
		} else {
			for (Object o : LATEST) {
				if (o instanceof AbstractRelic) {
					if(UnlockTracker.isRelicSeen(((AbstractRelic)o).relicId))
						continue;
				} else if (o instanceof AbstractCard) {
					if(UnlockTracker.isCardSeen(((AbstractCard)o).cardID))
						continue;
				}
				obtain(p, o);
				return true;
			}
		}
		return false;
	}
	
	private static void initLatest() {
		addLatest();
	}
	
	private static void addLatest(Object... list) {
		for (Object o : list)
			LATEST.add(o);
	}
	
	private static void initCheat() {
		addInit(INIT0, new TemporaryDeletion());
		addInit(INIT_G, new CrystalShield(), new HeadAttack());
		addInit(INIT_B, new HistoricalDocuments(), new Dream());
		addInit(INIT_R, new CyclicPeriapt(), new TreasureHunter());
		addInit(INIT_G, new FatalChain(), new DeathImprint());
		addInit(INIT_B, new Register(), new Recap());
		addInit(INIT_R, new AssaultLearning(), new RabbitOfFibonacci());
	}
	
	private static void addInit(ArrayList<ArrayList<Object>> init, Object... list) {
		ArrayList<Object> tmp = new ArrayList<Object>();
		for (Object o : list)
			tmp.add(o);
		init.add(tmp);
	}
	
	private static void cheatInit(AbstractPlayer p, ArrayList<Object> list) {
		for (Object o : list) {
			obtain(p, o);
		}
	}
	
	private void cheatInit(AbstractPlayer p) {
		if (!INIT0.isEmpty()) {
			cheatInit(p, INIT0.remove(0));
			return;
		}
		switch (p.chosenClass) {
		case DEFECT:
			if (!INIT_B.isEmpty()) {
				cheatInit(p, INIT_B.remove(0));
				return;
			}
			break;
		case IRONCLAD:
			if (!INIT_R.isEmpty()) {
				cheatInit(p, INIT_R.remove(0));
				return;
			}
			break;
		case THE_SILENT:
			if (!INIT_G.isEmpty()) {
				cheatInit(p, INIT_G.remove(0));
				return;
			}
			break;
		default:
		}
		if (!INIT.isEmpty()) {
			cheatInit(p, INIT.remove(0));
		} else {
			MODE = Mode.RELIC;
			
			receivePostDungeonInitialize();
		}
	}
	
	private static Object randomBonusReward() {
		ArrayList<Object> rewards = new ArrayList<Object>();
		ArrayList<Object> all = new ArrayList<Object>();
		all.addAll(CARDS);
		all.addAll(RELICS);
		for (AbstractRelic r : RELICS) {
			if (!r.isSeen) {
				rewards.add(r);
			}
		}
		for (AbstractCard c : CARDS) {
			if (!c.isSeen) {
				rewards.add(c);
			}
		}
		if (rewards.isEmpty()) {
			return all.get((int) (Math.random() * all.size()));
		} else {
			return rewards.get((int) (Math.random() * rewards.size()));
		}
	}
	
	@Override
	public void receivePostDungeonInitialize() {
		if (AbstractDungeon.floorNum > 1) {
			return;
		}
		if (config == null)
			this.initSavingConfig();
		AbstractPlayer p = AbstractDungeon.player;
		DragonStarHat.resetValue();
		Faith.reset();
		NoteOfAlchemist.setState(false);
		Mahjong.saveDefaultYama();
		// 初始遗物
		switch (MODE) {
		case BOX:
			obtain(p, new TestBox());
			break;
		case BOTH:
			obtain(p, randomBonusCard());
			obtain(p, randomBonusRelic());
			break;
		case RANDOM:
			if (!checkLatest(p))
				obtain(p, randomBonusReward());
			break;
		case CHEAT:
			cheatInit(p);
			break;
		case TEST:
			for (int i = 0; i < RELICS.size(); i++) {
				switch (RELICS.get(i).relicId) {
				case "D_4": 					continue; //*/	break;
				case "BouquetWithThorns":		continue; //*/	break;
				case "SevenDeadlySins":			continue; //*/	break;
				case "ObsoleteBoomerang":		continue; //*/	break;
				case "BlackFramedGlasses":		continue; //*/	break;
				case "PortableAltar":			continue; //*/	break;
				case "Register":				continue; //*/	break;
				case "EnergyCheck":				continue; //*/	break;
				case "InfectionSource":			continue; //*/	break;
				case "OneHitWonder":			continue; //*/	break;
				case "Recursion":				continue; //*/	break;
				case "Temperance":				continue; //*/	break;
				case "Justice":					continue; //*/	break;
				case "Fortitude":				continue; //*/	break;
				case "Charity":					continue; //*/	break;
				case "Hope":					continue; //*/	break;
				case "Prudence":				continue; //*/	break;
				case "AssaultLearning":			continue; //*/	break;
				case "Muramasa":				continue; //*/	break;
				case "HeartOfDaVinci":			continue; //*/	break;
				case "IncinerationGenerator":	continue; //*/	break;
				case "IndustrialRevolution":/*	continue; //*/	break;
				case "AscensionHeart":			continue; //*/	break;
				case "Nyarlathotep":			continue; //*/	break;
				case "RealStoneCalender":		continue; //*/	break;
				case "BalancedPeriapt":			continue; //*/	break;
				case "MagicalMallet":			continue; //*/	break;
				case "Laevatain":				continue; //*/	break;
				case "TimeTraveler":			continue; //*/	break;
				case "Maize":					continue; //*/	break;
				case "DragonStarHat":			continue; //*/	break;
				case "Nine":					continue; //*/	break;
				case "SaigiyounoYou":			continue; //*/	break;
				case "Motorcycle":				continue; //*/	break;
				case "DreamHouse":				continue; //*/	break;
				case "CrystalShield":			continue; //*/	break;
				case "BottledCurse":			continue; //*/	break;
				case "Alchemist":				continue; //*/	break;
				case "StringDisintegrator":		continue; //*/	break;
				case "HarvestTotem":			continue; //*/	break;
				case "BatchProcessingSystem":	continue; //*/	break;
				case "CardMagician":			continue; //*/	break;
				case "NegativeEmotionEnhancer":	continue; //*/	break;
				case "LackOfCard":				continue; //*/	break;
				case "Brilliant":				continue; //*/	break;
				case "BirthdayGift":			continue; //*/	break;
				case "IWantAll":				continue; //*/	break;
				case "Antiphasic":				continue; //*/	break;
				case "IntensifyImprint":		continue; //*/	break;
				default:						continue; //*/	break;
				}
				obtain(p, RELICS.get(i), true);
			}
			AbstractDungeon.player.masterDeck.clear();
			obtain(p, new DisillusionmentEcho(), 1);
			obtain(p, new HeadAttack(), 0);
			break;
		case RELIC:
			obtain(p, randomBonusRelic());
			break;
		case CARD:
			obtain(p, randomBonusCard());
			break;
		default:
			break;
		}
	}

	public static void unlock(Object o) {
		if (o instanceof AbstractRelic) {
			AbstractRelic r = (AbstractRelic) o;
			if (!UnlockTracker.isRelicSeen(r.relicId)) {
				UnlockTracker.markRelicAsSeen(r.relicId);
				info("成功解锁了未见过的 " + r.name);
			}
		} else if (o instanceof AbstractCard) {
			AbstractCard c = (AbstractCard) o;
			if (!UnlockTracker.isCardSeen(c.cardID)) {
				UnlockTracker.markCardAsSeen(c.cardID);
				info("成功解锁了未见过的 " + c.name);
			}
		}
	}
	
	public static void unlockAll() {
		info("开始解锁");
		for (AbstractCard c : CARDS)
			unlock(c);
		for (AbstractCard c : Sins.SINS)
			if (c instanceof AbstractTestCard)
				unlock(c);
		for (AbstractCard c : MAHJONGS)
			unlock(c);
		for (AbstractRelic r : RELICS)
			unlock(r);
	}
	
	private static final ArrayList<AbstractRelic> TO_OBTAIN = new ArrayList<AbstractRelic>();
	
	public static void obtainLater(AbstractRelic r) {
		TO_OBTAIN.add(r);
	}
	
	public static AbstractRelic randomBonusRelic() {
		ArrayList<AbstractRelic> unSeen = new ArrayList<AbstractRelic>();
		for (AbstractRelic r : RELICS) {
			if (!r.isSeen) {
				unSeen.add(r);
			}
		}
		if (!unSeen.isEmpty())
			return unSeen.get((int) (Math.random() * unSeen.size()));
		return RELICS.get((int) (Math.random() * (RELICS.size())));
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
	
	public static AbstractCard randomBonusCard() {
		if (MODE.cheat()) {
		}
		ArrayList<AbstractCard> unSeen = new ArrayList<AbstractCard>();
		for (AbstractCard c : CARDS) {
			if (!c.isSeen) {
				unSeen.add(c);
			}
		}
		if (!unSeen.isEmpty())
			return unSeen.get((int) (Math.random() * unSeen.size()));
		return CARDS.get((int) (Math.random() * (CARDS.size())));
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
			for (Iterator<AbstractRelic> i = p.relics.iterator(); i.hasNext();) {
				AbstractRelic temp = i.next();
				if (temp instanceof MyRelic) {
					((MyRelic)temp).preUpdate();
				}
			}
			this.updateGlow();
		}
	}

	@Override
	public void receivePostUpdate() {
		// TODO Auto-generated method stub
		AbstractPlayer p = AbstractDungeon.player;
		
		if (p != null) {
			if (!TO_OBTAIN.isEmpty() && AbstractDungeon.topLevelEffects.isEmpty()) {
				obtain(p, TO_OBTAIN.remove(0), true);
			}
			
			for (MyRelic r : MY_RELICS) {
				if (p.hasRelic(r.relicId)) {
					MyRelic m = (MyRelic)p.getRelic(r.relicId);
					setActivity(m);
					setShow(m);
				}
			}

			/*if (p.hasRelic("NoteOfAlchemist") && p.relics.get(0).relicId != "NoteOfAlchemist") {
				if (!NoteOfAlchemist.recorded()) {
					if ((AbstractDungeon.floorNum < 1) || (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss)) {
						NoteOfAlchemist.equipAction();
					} else if (AbstractDungeon.currMapNode != null) {
						NoteOfAlchemist.setState(true);
					}
				}
			} else if (CardCrawlGame.mode == GameMode.GAMEPLAY || CardCrawlGame.mode == GameMode.DUNGEON_TRANSITION) {
				NoteOfAlchemist.setState(false);
			}*/
			
			for (MyRelic r : MY_RELICS) {
				/*if (r instanceof NoteOfAlchemist) {
					continue;
				}*/
				try {
					if (MyRelic.tryEquip(r)) {
						r.getClass().getMethod("equipAction").invoke(null);
					} else if (MyRelic.tryUnequip(r)) {
						r.getClass().getMethod("unequipAction").invoke(null);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
			
			for (Iterator<AbstractRelic> i = p.relics.iterator(); i.hasNext();) {
				AbstractRelic temp = i.next();
				if (temp instanceof MyRelic) {
					((MyRelic)temp).postUpdate();
				}
			}
			
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
						AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.hoveredMonster;
	        			c.preApplyPowers(p, m);
	    				if (this.hasPrudence())
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
			
			if (p.hasRelic(makeID(HeartOfDaVinci.ID))) {
				if (HeartOfDaVinci.checkGetRelic()) {
					HeartOfDaVinci hdv = (HeartOfDaVinci) p.getRelic(makeID(HeartOfDaVinci.ID));
					hdv.onGetRelic(p.relics.get(HeartOfDaVinci.size()));
				}
			}
			
			SuperconductorNoEnergyPower.UpdateCurrentInstance();
			
		}
	}
	
	public static void setShow(MyRelic r) {
		r.show = AbstractDungeon.player.relics.size() < 26;
	}
	
	public static void setActivity(MyRelic relic) {
		int count = 0;
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (r.relicId.equals(relic.relicId))
				((MyRelic)r).isActive = count++ == 0;
	}
	
	//private static boolean needFix;
	
	public static final Properties DEFAULT = new Properties();
	public static SpireConfig config = null;
	
	private void initSavingConfig() {
		DEFAULT.setProperty("maxHPLost", "0");
		DEFAULT.setProperty("preMaxHP", "0");
		DEFAULT.setProperty("san", "100");
		DEFAULT.setProperty("recorded", "false");
		DEFAULT.setProperty("HatMaxStr", "0");
		DEFAULT.setProperty("FaithGained", "false");
		DEFAULT.setProperty("FaithPreGold", "0");
		DEFAULT.setProperty("MahjongTurns", "0");
		DEFAULT.setProperty("MahjongKang", "0");
		DEFAULT.setProperty("MahjongReach", "false");
		for (int i = 0; i < Mahjong.YAMA_NAME.length; i++)
			DEFAULT.setProperty(Mahjong.YAMA_NAME[i], "" + Mahjong.YAMA_DEFAULT[i]);
		for (String s : Mahjong.KANG_NAME)
			DEFAULT.setProperty(s, "-1");
		for (String s : Mahjong.DORA_NAME)
			DEFAULT.setProperty(s, "0");
		for (int i = 0; i < 13; i++)
			DEFAULT.setProperty(Mahjong.HAND_NAME + i, "0");
		try {
			config = new SpireConfig("TestMod", "Common", DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void receiveStartGame() {
		if (config == null)
			this.initSavingConfig();
		if (AbstractDungeon.player.hasRelic(makeID(PortableAltar.ID))) {
			PortableAltar.maxHPLost = config.getInt("maxHPLost");
		}
		Sins.preMaxHP = config.getInt("preMaxHP");
		if (Sins.preMaxHP != AbstractDungeon.player.maxHealth) {
			Sins.preMaxHP = AbstractDungeon.player.maxHealth;
			saveVariable("preMaxHP", Sins.preMaxHP);
		}
		Sins.isSelect = false;
		HeartOfDaVinci.clear();
		HeartOfDaVinci.init(AbstractDungeon.player.relics.size());
		TimeTraveler.load(config.getInt("san"));
		Motorcycle.loadGame();
		// NoteOfAlchemist.setState(config.getBool("recorded"));
		DragonStarHat.loadValue(config.getInt("HatMaxStr"));
		Faith.load(config.getBool("FaithGained"), config.getInt("FaithPreGold"));
		if (AbstractDungeon.player.hasRelic(makeID(Mahjong.ID))) {
			int[] yama = new int[37], kang = new int[config.getInt("MahjongKang")], hint = new int[kang.length + 1], hand = new int[13 - 3 * kang.length];
			for (int i = 0; i < 37; i++)
				yama[i] = config.getInt(Mahjong.YAMA_NAME[i]);
			for (int i = 0; i < kang.length; i++)
				kang[i] = config.getInt(Mahjong.KANG_NAME[i]);
			for (int i = 0; i < kang.length + 1; i++)
				hint[i] = config.getInt(Mahjong.DORA_NAME[i]);
			for (int i = 0; i < hand.length; i++)
				hand[i] = config.getInt(Mahjong.HAND_NAME + i);
			Mahjong.load(config.getInt("MahjongTurns"), config.getBool("MahjongReach"), yama, kang, hint, hand);
		}
		Automaton.loadMagicNumber();
	}
	
	public static void saveVariable(String key, int value) {
		config.setInt(key, value);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveVariable(String key, boolean value) {
		config.setBool(key, value);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void addEvents() {
		Object[] events = { BoxForYourself.class, PlateOfNloth.class };
		for (Object o : events) {
			Class<? extends AbstractEvent> c = (Class<? extends AbstractEvent>) o;
			try {
				BaseMod.addEvent((String) c.getField("ID").get(null), c);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void addPotions() {
		Object[] potions = { EscapePotion.class, TimePotion.class, SpacePotion.class };
		for (Object o : potions) {
			Class<? extends AbstractPotion> c = (Class<? extends AbstractPotion>) o;
			try {
				BaseMod.addPotion(c, null, null, null, (String) c.getField("POTION_ID").get(null));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void receivePostInitialize() {
		RelicSelectScreen.initialize();
		avoidFirstTime();
		//needFix = !BaseMod.hasModID("ZeroMaxHealthFix:");
		
		/**
		 * TODO since version 3.3.9
		 */
		checkOldID();
		
		addEvents();
		addPotions();
		
		if ("280 chan".equals(CardCrawlGame.playerName))
			unlockAll();
		
		/*
		initCheat();
		initLatest();
		*/
		
		// TODO
	}

	public static <T> T randomItem(ArrayList<T> list, Random rng) {
		return list.get(rng.random(list.size()) - 1);
	}
	
	public static <T> T randomItem(ArrayList<T> list, java.util.Random rng) {
		return list.get(rng.nextInt(list.size()));
	}
	
	@Override
	public void receiveOnBattleStart(AbstractRoom room) {
		PerfectComboAction.setRng();
		AbstractMahjongCard.setRng();
		Mahjong.setRng();
	}

	@Override
	public int receiveMapHPChange(int amount) {
		float tmp = amount * 1f;
		for (AbstractRelic r : AbstractDungeon.player.relics) {
			if (r instanceof MyRelic) {
				tmp = ((MyRelic)r).preChangeMaxHP(tmp);
			}
		}
		return (int)tmp;
	}
	
	public static String eventIMGPath(String ID) {
		String path = "resources/images/" + ID + ".jpg";
		if (fileExist(path))
			return path;
		path = "resources/images/" + ID + ".png";
		if (fileExist(path))
			return path;
		return eventIMGPath("relic1");
	}
	
	public static String relicIMGPath(String ID) {
		String path = "resources/images/relics/" + ID + ".png";
		if (fileExist(path))
			return path;
		return relicIMGPath("relic1");
	}
	
	public static String cardIMGPath(String ID) {
		String path = "resources/images/cards/" + ID + ".png";
		if (fileExist(path))
			return path;
		return cardIMGPath("relic1");
	}
	
	public static String powerIMGPath(String ID) {
		String path = "resources/images/powers/" + ID + ".png";
		if (fileExist(path))
			return path;
		return powerIMGPath("relic1");
	}
	
	public static boolean fileExist(String path) {
		return Gdx.files.internal(path).exists();
	}
	
}
