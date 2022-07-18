package testmod.relicsup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;

import testmod.potions.EscapePotion;
import testmod.utils.CounterKeeper;
import testmod.utils.InfiniteUpgradeRelic;

public class IWantAllUp extends AbstractUpgradedRelic implements ClickableRelic, CounterKeeper, InfiniteUpgradeRelic {
	public static final int COUNT = 10;
	
	private static final boolean REME = Loader.isModLoaded("REMEMod");
	private boolean victory = false;
	private static int mode = 0;
	
	public IWantAllUp() {
		super(RelicTier.SHOP, LandingSound.MAGICAL);
	}
	
	public void run(AbstractRelic r, AbstractUpgradedRelic u) {
		u.counter = Math.max(0, r.counter) + COUNT;
		u.updateDescription();
		((IWantAllUp) u).victory = true;
	}
	
	public void onEquip() {
		if (!this.hasStack("relicupgradelib.ui.RelicUpgradePopup", "replaceRelic")) {
			this.counter = COUNT;
			this.updateDescription();
			this.victory = !this.inCombat();
		}
	}
	
	public String getUpdatedDescription() {
		if (!this.isObtained)
			return DESCRIPTIONS[0] + 0 + DESCRIPTIONS[2];
		int tmp = 1 + remeWantAll();
		return DESCRIPTIONS[0] + mode + (mode > tmp ? DESCRIPTIONS[5] + (mode - tmp) + DESCRIPTIONS[6]
				: (DESCRIPTIONS[mode > 0 ? 2 + mode : (this.counter == -2 ? 1 : 2)]));
	}
	
	public void onVictory() {
		if (this.counter > 0) {
			this.togglePulse(this, victory = true);
			mode = 0;
		}
		this.updateDescription();
    }
	
	public void onEnterRoom(final AbstractRoom room) {
		if (this.victory) {
			this.togglePulse(this, victory = false);
		}
    }
	
	private RewardItem copy(RewardItem r) {
		RewardItem item = new RewardItem(new EscapePotion());
		item.type = RewardType.CARD;
		item.potion = null;
		item.text = RewardItem.TEXT[2];
		item.cards = r.cards.stream().map(AbstractCard::makeStatEquivalentCopy).collect(this.toArrayList());
		return item;
	}
	
	private void addReward() {
		list().addAll(card().map(this::copy).collect(toArrayList()));
		AbstractDungeon.combatRewardScreen.positionRewards();
	}
	
	private static boolean checkReward() {
		return list().stream().anyMatch(r -> r.type == RewardType.CARD);
	}
	
	private static ArrayList<RewardItem> list() {
		return AbstractDungeon.combatRewardScreen.rewards;
	}
	
	private static Stream<RewardItem> card() {
		return list().stream().filter(r -> r.type == RewardType.CARD);
	}
	
	private int cardNum() {
		return p().relics.stream().map(r -> get(r::changeNumberOfCardsInReward)).reduce(t(), this::chain).apply(3);
	}
	
	private int remeWantAll() {
		return REME && p().hasRelic("REME_IWantAll") ? 1 : 0;
	}
	
	@Override
	public void onRightClick() {
		if (AbstractDungeon.screen == CurrentScreen.MAP) {
			mode++;
			if (mode == 1 && !p().hasRelic("Singing Bowl")) {
				mode = 2;
			}
			if (AbstractDungeon.combatRewardScreen != null && list() != null) {
				int size = card().map(r -> r.cards.size()).filter(s -> s != 0).findFirst().orElse(cardNum());
				mode %= 2 + size + remeWantAll();
			}
			this.updateDescription();
		} else if (this.victory && checkReward()) {
			int size = card().map(r -> r.cards.size()).filter(s -> s != 0).findFirst().orElse(cardNum());
			mode %= 2 + size + remeWantAll();
			this.updateDescription();
			if (mode == 0) {
				addReward();
				this.counter--;
				if (this.counter == 0) {
					this.counter = -2;
					this.updateDescription();
				}
			} else if (mode == 1) {
				if (p().hasRelic("Singing Bowl")) {
					p().getRelic("Singing Bowl").flash();
					CardCrawlGame.sound.playA("SINGING_BOWL", MathUtils.random(-0.2F, 0.1F));
					card().collect(toArrayList()).forEach(this::sing);
				}
			} else {
				if (remeWantAll() == 1 && mode == 2) {
					p().getRelic("REME_IWantAll").flash();
					CardCrawlGame.sound.playA("SINGING_BOWL", MathUtils.random(-0.2F, 0.1F));
					card().collect(toArrayList()).forEach(this::reme);
				} else {
					mode -= remeWantAll() + 2;
					card().collect(toArrayList()).forEach(this::pick);
					mode += remeWantAll() + 2;
				}
				AbstractDungeon.combatRewardScreen.positionRewards();
				if (list().isEmpty()) {
					AbstractDungeon.combatRewardScreen.hasTakenAll = true;
					AbstractDungeon.overlayMenu.proceedButton.show();
				}
			} 
			if (mode != 0 || counter == -2)
				this.togglePulse(this, victory = false);
		}
	}
	
	private void reme(RewardItem r) {
		r.cards.forEach(c -> AbstractDungeon.effectsQueue.add(new FastCardObtainEffect(c, c.current_x, c.current_y)));
		if (r.cards.size() > 0) {
			p().increaseMaxHp(r.cards.size(), true);
		}
		list().remove(r);
	}
	
	private void sing(RewardItem r) {
		p().increaseMaxHp(2, true);
		list().remove(r);
	}
	
	private void pick(RewardItem r) {
		AbstractCard c = r.cards.get(mode);
		recordMetrics(c, r.cards);
		AbstractDungeon.effectsQueue.add(new FastCardObtainEffect(c, c.current_x, c.current_y));
		list().remove(r);
	}
	
	private void recordMetrics(AbstractCard card, ArrayList<AbstractCard> cards) {
		HashMap<String, Object> choice = new HashMap<String, Object>();
		ArrayList<String> notpicked = cards.stream().filter(c -> !card.equals(c)).map(c -> c.getMetricID())
				.collect(toArrayList());
		choice.put("picked", card.getMetricID());
		choice.put("not_picked", notpicked);
		choice.put("floor", Integer.valueOf(AbstractDungeon.floorNum));
		CardCrawlGame.metricData.card_choices.add(choice);
	}
	
	public static void loadVictory() {
		MISC.relicStream(IWantAllUp.class).forEach(AbstractRelic::onVictory);
	}

}