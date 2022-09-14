package testmod.relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.mymod.TestMod;
import testmod.patches.SwFPatch;
import testmod.relicsup.NyarlathotepUp;

public class Nyarlathotep extends AbstractTestRelic {

	public static final String[] CARD_IDs = { "Force Field" };
	public static final String[] POWER_IDs = { "testmod-PlagueActPower", "Amplify", "Heatsink", "Storm", "Curiosity",
			"Recycle_Bin_Power", "reliquary:StormPlus", "VUPShionMod:TwoPowerPower", "VUPShionMod:ThreePowerPower",
			"VUPShionMod:FourPowerPower" };
	public static final String[] RELIC_IDs = { "testmod-MaizeUp", "testmod-MuramasaUp", "testmod-RestrainedUp", "Clover",
			"Bird Faced Urn", "Mummified Hand", "OrangePellets", "SynthVMod:C4", "paleoftheancients:SoulOfTheDefect",
			"Replay:Rubber Ducky", "Dota2Spire:ArcaneBoots", "Dota2Spire:EtherealBlade", "DemoExt:GalacticMedalOfValor",
			"Dota2Spire:OrchidMalevolence", "Dota2Spire:AghanimScepter", "Steelbody", "SynthVMod:MejaisSoulstealer",
			"youkari:Boundary_crack", "BirthdayGift-Icosahedron", "RU OrangePellets", "RU Bronze Scales",
			"RU Bird Faced Urn", "Kaltsit_LearningSimulationPower" };
	public static final String[] RELIC_ON_PLAY_IDs = { "reliquary:IridiumChain" };
	public static final ArrayList<String> CARD_LIST = new ArrayList<String>();
	public static final ArrayList<String> POWER_LIST = new ArrayList<String>();
	public static final ArrayList<String> RELIC_LIST = new ArrayList<String>();
	
	private static void addToList(ArrayList<String> target, String id) {
		if (!target.contains(id))
			target.add(id);
	}
	
	public static void addCardList(ArrayList<String> list) {
		CARD_LIST.addAll(list);
	}
	
	public static void addCardList(String... list) {
		Stream.of(list).forEach(s -> addToList(CARD_LIST, s));
	}
	
	public static void addPowerList(ArrayList<String> list) {
		POWER_LIST.addAll(list);
	}
	
	public static void addPowerList(String... list) {
		Stream.of(list).forEach(s -> addToList(POWER_LIST, s));
	}
	
	public static void addRelicList(ArrayList<String> list) {
		RELIC_LIST.addAll(list);
	}
	
	public static void addRelicList(String... list) {
		Stream.of(list).forEach(s -> addToList(RELIC_LIST, s));
	}
	
	private boolean valid(AbstractCard c) {
		return Stream.of(CARD_IDs).anyMatch(c.cardID::equals) || CARD_LIST.stream().anyMatch(c.cardID::equals);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive && this.inCombat()) {
			SwFPatch.BingoExhaustPatch.exhaust = 0;
		}
	}
	
	public void atPreBattle() {
		if (this.isActive)
			SwFPatch.BingoExhaustPatch.exhaust = 0;
	}
	
	private static boolean isThis(AbstractRelic r) {
		return r instanceof NyarlathotepUp || r instanceof Nyarlathotep;
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		this.triggerExhaustFor(c);
		this.triggerDiscardFor(c);
		this.triggerUsePowerCard(c, action);
		this.show();
	}
	
	private void triggerExhaustFor(AbstractCard c) {
		p().relics.stream().filter(not(Nyarlathotep::isThis)).forEach(r -> r.onExhaust(c));
		p().powers.forEach(p -> p.onExhaust(c));
		c.triggerOnExhaust();
		SwFPatch.BingoExhaustPatch.exhaust(true);
	}
	
	private void triggerDiscardFor(AbstractCard c) {
        c.triggerOnManualDiscard();
        ++GameActionManager.totalDiscardedThisTurn;
		if (AbstractDungeon.actionManager.turnHasEnded)
			return;
		p().updateCardsOnDiscard();
		p().relics.stream().filter(not(Nyarlathotep::isThis)).forEach(r -> r.onManualDiscard());
	}
	
	private String checkID(Object i) {
		if (i instanceof AbstractPower)
			return ((AbstractPower)i).ID;
		if (i instanceof AbstractRelic)
			return ((AbstractRelic)i).relicId;
		return ((AbstractCard)i).cardID;
	}
	
	private <T> Stream<T> get(ArrayList<T> list, String id) {
		return list.stream().filter(i -> id.equals(checkID(i)));
	}
	
	private void triggerUsePowerCard(AbstractCard c, UseCardAction a) {
		AbstractCard tmp = c.makeSameInstanceOf();
		tmp.type = CardType.POWER;
		Stream.concat(Stream.of(POWER_IDs), POWER_LIST.stream()).distinct().filter(p()::hasPower)
				.flatMap(id -> get(p().powers, id)).forEach(p -> p.onUseCard(tmp, a));
		Stream.concat(Stream.of(RELIC_IDs), RELIC_LIST.stream()).distinct().filter(p()::hasRelic)
				.flatMap(id -> get(p().relics, id)).forEach(r -> r.onUseCard(tmp, a));
		Stream.of(RELIC_ON_PLAY_IDs).filter(p()::hasRelic).flatMap(id -> get(p().relics, id)).forEach(
				r -> r.onPlayCard(tmp, (a.target instanceof AbstractMonster) ? (AbstractMonster) a.target : null));
		this.combatCards().filter(this::valid).forEach(b -> b.triggerOnCardPlayed(c));
		p().hand.group.forEach(b -> b.triggerOnOtherCardPlayed(c));
	}
	
	public void onExhaust(final AbstractCard c) {
		this.triggerDiscardFor(c);
		this.show();
    }
	
	public void onManualDiscard() {
		if (!p().discardPile.isEmpty()) {
			this.triggerExhaustFor(p().discardPile.getTopCard());
			this.show();
		}
    }
	
}