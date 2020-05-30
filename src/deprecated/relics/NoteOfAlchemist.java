package deprecated.relics;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import mymod.TestMod;
import relics.MyRelic;

/**
 * @deprecated
 */
public class NoteOfAlchemist extends MyRelic{
	private static final Logger logger = LogManager.getLogger(NoteOfAlchemist.class.getName());
	public static final String ID = "NoteOfAlchemist";//遗物Id，添加遗物、替换遗物时填写该id而不是遗物类名。
	public static final String IMG = "resources/images/NoteOfAlchemist.png";//遗物图片路径
	
	public static final String DESCRIPTION = "拾起时失去现有的除了该遗物的所有遗物。失去的遗物按照该规则获得新遗物:普通->罕见；罕见->稀有； 稀有->增加 #b5 点最大生命；Boss->普通； 商店->Boss；初始->商店。";//遗物效果的文本描叙。
	//特殊格式： 1.文本描叙中#r、#y、#b、#g分别能使文本变成红、黄、蓝、绿色。
	//         使用方法：将需要变色的部分无空格放在#r(#y/#b/#g)后面，然后将这一块前后用空格与其他文本隔开。
	//         例：public static final DESCRIPTION = "回合开始时获得 #b3 点力量.";
	//       2.文本描叙中[R]、[G]、[B]分别对应战士、猎手、机器人的能量。
	//         使用方法同上，不再赘叙。
	
	private static boolean recorded = false;
	private static ArrayList<AbstractRelic> rewards = new ArrayList<AbstractRelic>();
	
	public static void saveState() {
		TestMod.saveVariable("recorded", recorded);
	}
	
	public static boolean recorded() {
		return recorded;
	}
	
	public static void setState(boolean state) {
		recorded = state;
	}
	
	public NoteOfAlchemist() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
		//参数：ID-遗物Id，new Texture(Gdx.files.internal(IMG))-遗物图片，new Texture(Gdx.files.internal(OUTLINE))-遗物轮廓，RelicTier.BOSS-遗物种类，LandingSound.FLAT-遗物音效。
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}//文本更新方法，当你修改了DESCRIPTION时，调用该方法。
	
	public AbstractRelic makeCopy() {
		return new NoteOfAlchemist();
	}//复制该遗物信息的方法。
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		show();
		rewards.clear();
    }//触发时机：当玩家获得该遗物时。(参考灵体外质、诅咒钥匙、天鹅绒项圈等)
	
	public static void equipAction() {
		setState(true);
		removeRelics();
		showReward();
	}
	
	public static void removeRelics() {
		ArrayList<AbstractRelic> relics = new ArrayList<AbstractRelic>();
		AbstractPlayer p = AbstractDungeon.player;
		relics.addAll(p.relics);	// 角色当前遗物列表
		logger.info("获得角色当前遗物列表");
		int[] counts = new int[8];
		for (int i = 0; relics.size() > i; i++) {						// 统计
			AbstractRelic current = relics.get(i);	// 获取列表中第i个遗物
			if (current.relicId.equals(ID)) {		// 如果是该遗物本身(虽然理论上不会发生)，不执行任何操作
				logger.info("该遗物本身");
				continue;
			}
			switch (current.tier) {						// 增加对应遗物种类计数
			case STARTER:	counts[1]++; logger.info("当前初始遗物" + current.name); break;
			case COMMON:	counts[2]++; logger.info("当前普通遗物" + current.name); break;
			case UNCOMMON:	counts[3]++; logger.info("当前罕见遗物" + current.name); break;
			case RARE:		counts[4]++; logger.info("当前稀有遗物" + current.name); break;
			case SPECIAL:	counts[5]++; logger.info("当前特殊遗物" + current.name); break;
			case BOSS:		counts[6]++; logger.info("当前BOSS遗物" + current.name); break;
			case SHOP:		counts[7]++; logger.info("当前商店遗物" + current.name); break;
			default:		counts[0]++; logger.info("当前未知遗物" + current.name);
			}
		}
		for (; p.relics.size() > 1;) {
			loseFirstOtherRelic(p);
	    }
		logger.info("删除所有遗物");
		
		addReward(counts);
		logger.info("添加遗物到数组完毕");
		logger.info(rewards);
	}
	
	private static void loseFirstOtherRelic(AbstractPlayer p) {
		AbstractRelic toRemove = null;
		for (int i = 0; i < p.relics.size(); i++) {
			if (!p.relics.get(i).relicId.equals(ID)) {
				 toRemove = p.relics.get(i);
				 break;
			}
		}
		
		toRemove.onUnequip();
		p.relics.remove(toRemove);
		p.reorganizeRelics();
	}
	
	private static void addReward(int[] counts) {
		for (int tier = 1; nonZero(counts) && tier < 8; tier++) {
			if (tier != 5) {		// 非事件遗物
				for (; 0 < counts[tier]; counts[tier]--) {
					addRelic(tier);
				}
			}
		}
	}
	
	private static void addRelic(int deletedTier) {
		switch (deletedTier) {
		case 1: logger.info("获得随机商店遗物" + addRelic(RelicTier.SHOP));			break;
		case 2: logger.info("获得随机罕见遗物" + addRelic(RelicTier.UNCOMMON));		break;
		case 3: logger.info("获得随机稀有遗物" + addRelic(RelicTier.RARE));			break;
		case 4: logger.info("生命上限+5");	 AbstractDungeon.player.increaseMaxHp(5, true); break;
		case 6: logger.info("获得随机普通遗物" + addRelic(RelicTier.COMMON));		break;
		case 7: logger.info("获得随机Boss遗物" + addRelic(RelicTier.BOSS));			break;
		default:
		}
	}
	
	private static boolean prohibit(String id) {
		String[] prohibit = {"Tiny House", "Cauldron", "Orrery"};
		for (String p : prohibit) {
			if (id.equals(p)) {
				return true;
			}
		}
		return false;
	}
	
	private static String addRelic(RelicTier tier) {
		AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(tier);
		if (prohibit(r.relicId)) {
			return addRelic(tier);
		}
		
		if (tier == RelicTier.BOSS && r.relicId.equals(ID)) {
			return addRelic(tier);
		}
		for (int i = 0; i < rewards.size(); i++) {
			if (rewards.get(i).name.equals(r.name)) {
				return addRelic(tier);
			}
		}
		rewards.add(r);
		return r.name;
	}
	
	private static boolean nonZero(int[] arr) {	// 给onEquip方法使用
		for (int i = 0; i < arr.length; i++)
			if (arr[i] != 0)
				return true;
		return false;
	}
	
	public static void showReward() {
		AbstractDungeon.getCurrRoom().rewards.clear();
		for (AbstractRelic t : rewards) {
			AbstractDungeon.getCurrRoom().addRelicToRewards(t);
		}
		AbstractDungeon.combatRewardScreen.open("炼金成功");
		while (AbstractDungeon.combatRewardScreen.rewards.size() > rewards.size()) {
			AbstractDungeon.combatRewardScreen.rewards.remove(rewards.size());
		}
		rewards.clear();
		AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.0F;
	}
	
}