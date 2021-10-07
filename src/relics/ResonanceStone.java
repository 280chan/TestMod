package relics;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class ResonanceStone extends AbstractTestRelic {
	public static final String ID = "ResonanceStone";
	
	private HashMap<AbstractCard, Integer> previous = new HashMap<AbstractCard, Integer>();
	boolean init = true;
	
	public ResonanceStone() {
		super(ID, RelicTier.UNCOMMON, LandingSound.MAGICAL);
		this.counter = 1;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onMasterDeckChange() {
		AbstractPlayer p = AbstractDungeon.player;
		previous.keySet().retainAll(p.masterDeck.group);
		p.masterDeck.group.stream().forEach(c -> previous.putIfAbsent(c, c.timesUpgraded));
	}
	
	public void update() {
		super.update();
		AbstractPlayer p = AbstractDungeon.player;
		if (!this.isObtained || p == null || p.masterDeck == null)
			return;
		if (init) {
			previous.putAll(p.masterDeck.group.stream().collect(Collectors.toMap(t(), c -> c.timesUpgraded)));
			init = false;
		} else {
			int tmp = this.counter;
			previous.entrySet().stream().filter(e -> delta(e) > 0).map(split(e -> e.getKey(), this::delta))
					.forEach(consumer(this::upgrade));
			previous.entrySet().stream().filter(e -> delta(e) > 0).forEach(e -> e.setValue(e.getKey().timesUpgraded));
			if (this.counter > tmp)
				this.getNumberList(tmp, this.counter).forEach(i -> {
					p.increaseMaxHp(i, true);
					p.gainGold(i);
				});
		}
	}
	
	private void upgrade(AbstractCard c) {
		c.upgrade();
		this.counter++;
	}
	
	private void upgrade(AbstractCard a, int b) {
		this.getNaturalNumberList(b).forEach(i -> AbstractDungeon.player.masterDeck.group.stream()
				.filter(c -> c.cardID.equals(a.cardID)).filter(c -> c.canUpgrade()).forEach(this::upgrade));
	}
	
	private int delta(Entry<AbstractCard, Integer> e) {
		return e.getKey().timesUpgraded - e.getValue();
	}

}