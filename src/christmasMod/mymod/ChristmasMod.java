package christmasMod.mymod;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import christmasMod.cards.*;
import christmasMod.relics.*;
import christmasMod.utils.ChristmasMiscMethods;
import mymod.TestMod;

/**
 * @author 彼君不触
 * @version 12/26/2020
 * @since 12/18/2018
 */

public class ChristmasMod implements EditRelicsSubscriber, EditCardsSubscriber, EditStringsSubscriber,
		OnStartBattleSubscriber, ChristmasMiscMethods {
	public static final String MOD_PREFIX = "ChristmasMod";
	private static ChristmasMod currentRunningMod;

	public static void initialize() {
		TestMod.subscribeSubModClass(new ChristmasMod());
	}
	
	public ChristmasMod() {
		currentRunningMod = this;
	}

	@Override
	public void receiveEditRelics() {
		Stream.of(new ChristmasGift(), new GiftOfSatan()).peek(TestMod.RELICS::add).forEach(RELICS::add);
	}

	private static String stringsPathFix(String s) {
		return "christmasResources/strings/ChristmasMod" + s + ".json";
	}

	private static String readString(String type) {
		return Gdx.files.internal(stringsPathFix(lanPostfix(type))).readString(String.valueOf(StandardCharsets.UTF_8));
	}

	private static String langPostfix = null;
	
	private static String lanPostfix(String s) {
		if (langPostfix != null)
			return s + langPostfix;
		// TODO
		switch (Settings.language) {
		case ZHS:
		case ENG:
			langPostfix = "_" + Settings.language.name().toLowerCase();
			break;
		case ZHT:
			langPostfix = "_zhs";
		default:
			langPostfix = "_eng";
			break;
		}
		return s + langPostfix;
	}
	
	@Override
	public void receiveEditStrings() {
		BaseMod.loadCustomStrings(RelicStrings.class, readString("relics"));
		BaseMod.loadCustomStrings(PowerStrings.class, readString("powers"));
		BaseMod.loadCustomStrings(CardStrings.class, readString("cards"));
	}

	@Override
	public void receiveEditCards() {
		Stream.of(new GiftLuck(), new GiftMagic(), new GiftExplosion(), new GiftHypnosis(), new GiftIron(),
				new GiftVoid(), new GiftKakaa(), new GiftDisturb(), new GiftInfinite(), new GiftDamaged())
				.forEach(GIFTS::add);
		Stream.of(new Plague(), new Famine(), new Death(), new War()).forEach(DISASTERS::add);
		Stream.of(GIFTS, DISASTERS).flatMap(l -> l.stream()).forEach(BaseMod::addCard);
	}
	
	public static ArrayList<AbstractCard> GIFTS = new ArrayList<AbstractCard>();
	public static ArrayList<AbstractCard> DISASTERS = new ArrayList<AbstractCard>();
	public static ArrayList<AbstractRelic> RELICS = new ArrayList<AbstractRelic>();

	public static AbstractCard randomGift(boolean upgraded) {
		return currentRunningMod.randomCard(GIFTS, upgraded);
	}
	
	public static AbstractCard randomDisaster(boolean upgraded) {
		return currentRunningMod.randomCard(DISASTERS, upgraded);
	}
	
	public static AbstractRelic randomRelic() {
		return RELICS.get((int) (Math.random() * RELICS.size())).makeCopy();
	}

	@Override
	public void receiveOnBattleStart(AbstractRoom room) {
		this.setRNG(AbstractDungeon.miscRng);
		this.setCardRNG(AbstractDungeon.cardRng);
	}
	
}
