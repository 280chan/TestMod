package relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.QueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mymod.TestMod;

public class BloodSacrificeSpiritualization extends AbstractTestRelic {
	
	public BloodSacrificeSpiritualization() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public void atBattleStart() {
		this.addTmpActionToBot(() -> {
			AbstractPlayer p = AbstractDungeon.player;
			CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
			g.group = Stream.of(p.discardPile, p.hand, p.drawPile).flatMap(c -> c.group.stream())
					.collect(this.collectToArrayList());
			p.hand.group.forEach(AbstractCard::beginGlowing);
	        int amount = Math.max(p.maxHealth / 10, 1);
			if (g.group.isEmpty()) {
				return;
			}
			String info = "跳过或失去" + amount + "点生命来选择1张牌尝试永久升级并打出(亮边的为手牌)";
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, true, false);
			AbstractDungeon.overlayMenu.cancelButton.show("跳过");
			this.addTmpActionToTop(() -> {
				if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
					p.damage(new DamageInfo(p, amount, DamageType.HP_LOSS));
					AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
					this.upgrade(p, c);
					this.addTmpActionToTop(() -> this.playCard(p, c));
					AbstractDungeon.gridSelectScreen.selectedCards.clear();
				}
			});
		});
    }
	
	private boolean checkMonster(AbstractMonster m) {
		return !(m.isDead || m.halfDead || m.escaped || m.isEscaping || m.isDying);
	}
	
	private void playCard(AbstractPlayer p, AbstractCard c) {
		ArrayList<AbstractMonster> list = AbstractDungeon.getMonsters().monsters.stream().filter(this::checkMonster)
				.collect(this.collectToArrayList());
		AbstractMonster m = list.isEmpty() ? null : list.get(AbstractDungeon.cardRandomRng.random(0, list.size() - 1));
		CardGroup g = getSource(p, c);
		if (g != null)
			g.group.remove(c);
		else
			TestMod.info("CardGroup == null ???");
        AbstractDungeon.getCurrRoom().souls.remove(c);
        c.freeToPlayOnce = true;
        p.limbo.group.add(c);
        c.current_y = (-200.0F * Settings.scale);
        c.target_x = (Settings.WIDTH / 2.0F + 200.0F * Settings.scale);
        c.target_y = (Settings.HEIGHT / 2.0F);
        c.targetAngle = 0.0F;
        c.lighten(false);
        c.drawScale = 0.12F;
        c.targetDrawScale = 0.75F;
		if (!c.canUse(p, m)) {
			this.addToTop(new UnlimboAction(c));
			this.addToTop(new DiscardSpecificCardAction(c, p.limbo));
			this.addToTop(new WaitAction(0.4F));
		} else {
			c.applyPowers();
			this.addToTop(new QueueCardAction(c, m));
			this.addToTop(new UnlimboAction(c));
			this.addToTop(new WaitAction(Settings.ACTION_DUR_FASTER));
		}
	}
	
	private void upgrade(AbstractPlayer p, AbstractCard card) {
		if (card.canUpgrade()) {
			card.upgrade();
		}
		p.masterDeck.group.stream().filter(c -> c.uuid.equals(card.uuid) && c.canUpgrade()).limit(1)
				.forEach(AbstractCard::upgrade);
	}

	private static CardGroup getSource(AbstractPlayer p, AbstractCard c) {
		return Stream.of(p.discardPile, p.hand, p.drawPile).filter(g -> g.contains(c)).findAny().orElse(null);
	}

	public boolean canSpawn() {
		return Settings.isEndless || !(AbstractDungeon.actNum > 1);
	}
	
}