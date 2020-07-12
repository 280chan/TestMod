package utils.Mahjong;

import cards.mahjong.AbstractMahjongCard;

import java.util.ArrayList;

public class MahjongHand {
    private ArrayList<AbstractMahjongCard> hand;
    public MahjongHand() {
        this.hand = new ArrayList<AbstractMahjongCard>(14);
    }

    /** 初始化手牌 */
    public boolean InitHandCard(MahjongWall wall) {
        if (!wall.isFull()) {
            return false;
        }else {
            while (this.hand.size() < 13) {
                this.hand.add(wall.DrawCard());
                wall.DrawCard();
                wall.DrawCard();
                wall.DrawCard();
            }
            return true;
        }

    }

    public boolean CanReach() {
        return false;
        // todo
    }
    /** 是否听牌 */
    public boolean isReady() {
        // todo
        return false;
    }
}
