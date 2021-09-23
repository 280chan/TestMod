
package cards.colorless;

import cards.AbstractTestCard;
import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class TreasureHunter extends AbstractTestCard {
	public static final String ID = "TreasureHunter";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final int COST = 2;
	private static final int BASE_DMG = 15;

	public TreasureHunter() {
		super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
		this.baseDamage = BASE_DMG;
		this.exhaust = true;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpActionToBot(() -> {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.SLASH_DIAGONAL));
			m.damage(new DamageInfo(p, this.damage, this.damageTypeForTurn));
			boolean toDeck = this.upgraded && (m.isDying || m.currentHealth <= 0) && (!m.halfDead);
			
			AbstractDungeon.cardRewardScreen.customCombatOpen(cards(), CardRewardScreen.TEXT[1], false);
			this.addTmpActionToTop(() -> {
				if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
					AbstractCard huntedCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeCopy();
					huntedCard.current_x = (-1000.0F * Settings.scale);
					
					if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
						AbstractDungeon.effectList.add(this.handFull() ? new ShowCardAndAddToDiscardEffect(huntedCard)
								: new ShowCardAndAddToHandEffect(huntedCard));
					}
					if (toDeck) {
						AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(huntedCard.makeCopy(),
								Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
					}
					AbstractDungeon.cardRewardScreen.discoveryCard = null;
				}
			});
		});
	}

	private ArrayList<AbstractCard> cards() {
		CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
		g.group = AbstractDungeon.rareCardPool.group.stream().collect(this.collectToArrayList());
		g.shuffle(AbstractDungeon.cardRandomRng);
		g.group = g.group.stream().limit(3).collect(this.collectToArrayList());
		g.group.stream().peek(c -> c.target_y = Settings.HEIGHT * 0.45F).map(c -> c.cardID)
				.forEach(UnlockTracker::markCardAsSeen);
		float deltaX = AbstractCard.IMG_WIDTH + 40.0F * Settings.scale;
		for (int i = 0; i < 3; i++)
			g.group.get(i).target_x = Settings.WIDTH / 2.0F + (i - 1) * deltaX;
		return g.group;
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeDamage(5);
			this.rawDescription = UPGRADED_DESCRIPTION;
			initializeDescription();
		}
	}
}