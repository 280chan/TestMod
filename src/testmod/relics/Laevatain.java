package testmod.relics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class Laevatain extends AbstractTestRelic {
	public static final UIStrings UI = MISC.uiString();
	
	public void onEquip() {
		CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		for (int i = 0; i < 3; i++) {
			if (p().relics.stream().anyMatch(r -> "Omamori".equals(r.relicId) && r.counter != 0)) {
				((Omamori) p().relics.stream().filter(r -> "Omamori".equals(r.relicId) && r.counter != 0).findFirst()
						.orElse(null)).use();
			} else {
				ArrayList<AbstractCard> tmp = AbstractDungeon.curseCardPool.group.stream()
						.filter(c -> c.rarity == CardRarity.CURSE).collect(toArrayList());
				Collections.shuffle(tmp, new Random(AbstractDungeon.cardRng.randomLong()));
				AbstractCard curse = tmp.get(0);
				UnlockTracker.markCardAsSeen(curse.cardID);
				group.addToBottom(curse.makeCopy());
				tmp.clear();
			}
		}
		AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, UI.TEXT[0]);
	}
	
	public void atPreBattle() {
		this.counter = 0;
		this.show();
		this.atb(apply(p(), new StrengthPower(p(), 3)));
	}
	
	private int count(Stream<AbstractCard> s) {
		return (int) s.filter(c -> c.type == CardType.CURSE).count();
	}
	
	private int count() {
		int tmp = count(p().masterDeck.group.stream());
		return tmp > 0 ? tmp : count(combatCards());
	}
	
	public void atTurnStart() {
		this.counter++;
		if (this.counter == 3) {
			this.counter = 0;
			if (Stream.concat(p().masterDeck.group.stream(), combatCards()).anyMatch(c -> c.type == CardType.CURSE)) {
				atb(apply(p(), new StrengthPower(p(), count())));
				this.show();
			}
		}
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
}