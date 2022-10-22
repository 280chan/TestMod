package testmod.cards.colorless;

import testmod.actions.WormholeAction;
import testmod.cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class Wormhole extends AbstractTestCard {
	public Wormhole() {
		super(1, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY);
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpActionToBot(() -> {
			CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
			this.combatCards().forEach(g.group::add);
			g.removeCard(this);
			p.hand.group.forEach(AbstractCard::beginGlowing);
			print("虫洞: Cardgroup大小=" + g.size());
			this.att(new WormholeAction(g, m, !this.upgraded));
		});
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upDesc();
		}
	}
}