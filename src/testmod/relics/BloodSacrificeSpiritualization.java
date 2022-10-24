package testmod.relics;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import testmod.relicsup.BloodSacrificeSpiritualizationUp;

public class BloodSacrificeSpiritualization extends AbstractTestRelic {
	public static final UIStrings UI = MISC.uiString();
	
	private void play(AbstractCard c, boolean purge) {
		c.purgeOnUse = purge;
		att(new NewQueueCardAction(c, true, false, true));
	}
	
	private Consumer<AbstractCard> play(int time) {
		return c -> {
			get(c).remove(c);
			AbstractDungeon.getCurrRoom().souls.remove(c);
			for (int i = 1; i < time; i++)
				play(c.makeSameInstanceOf(), true);
			play(c, false);
		};
	}
	
	public void atBattleStart() {
		if (!this.isActive || relicStream(BloodSacrificeSpiritualizationUp.class).count() > 0)
			return;
		this.addTmpActionToBot(() -> {
			CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
			g.group = this.combatCards().collect(this.toArrayList());
			p().hand.group.forEach(AbstractCard::beginGlowing);
			int amount = Math.max(p().maxHealth / 10, 1);
			if (g.group.isEmpty()) {
				return;
			}
			String info = UI.TEXT[0] + amount + UI.TEXT[1];
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, true, false);
			AbstractDungeon.overlayMenu.cancelButton.show(UI.TEXT[2]);
			this.addTmpActionToTop(() -> {
				if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
					p().damage(new DamageInfo(p(), amount, DamageType.HP_LOSS));
					AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
					int size = (int) this.relicStream(BloodSacrificeSpiritualization.class).count();
					for (int i = 0; i < size; i++) {
						this.upgrade(c);
					}
					this.play(size).accept(c);
					AbstractDungeon.gridSelectScreen.selectedCards.clear();
					this.grayscale = true;
				}
			});
		});
	}

	public void justEnteredRoom(AbstractRoom room) {
		this.grayscale = false;
	}
	
	private void upgrade(AbstractCard card) {
		if (card.canUpgrade()) {
			card.upgrade();
		}
		p().masterDeck.group.stream().filter(c -> c.uuid.equals(card.uuid) && c.canUpgrade()).limit(1)
				.forEach(AbstractCard::upgrade);
	}

	private static ArrayList<AbstractCard> get(AbstractCard c) {
		return Stream.of(MISC.p().discardPile, MISC.p().hand, MISC.p().drawPile).filter(g -> g.contains(c))
				.map(g -> g.group).findAny().orElse(new ArrayList<AbstractCard>());
	}

	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}
	
}