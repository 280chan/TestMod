package testmod.cards;

import java.util.ArrayList;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class AbstractUpdatableCard extends AbstractTestCard {
	public static final ArrayList<AbstractUpdatableCard> TO_UPDATE = new ArrayList<AbstractUpdatableCard>();
	protected boolean onMonster = false;
	private String originalDesc;
	
	public AbstractUpdatableCard() {
		this.changeDescription(this.desc(), true);
	}

	protected boolean isHovered() {
		return this.hb.hovered;
	}
	
	protected void changeDescription(String desc, boolean save) {
		if (save)
			this.originalDesc = desc;
		this.rawDescription = desc;
		this.initializeDescription();
	}
	
	public void resetDescription() {
		this.rawDescription = this.originalDesc;
		this.initializeDescription();
	}
	
	public abstract void preApplyPowers(AbstractPlayer p, AbstractMonster m);
}