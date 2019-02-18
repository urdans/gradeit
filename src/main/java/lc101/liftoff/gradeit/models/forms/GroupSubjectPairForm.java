package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.data.IGroupSubjectPair;

import java.util.ArrayList;
import java.util.List;

public class GroupSubjectPairForm {
    private int selectedPair;

    public class Pair {
        public int groupingId;
        public int groupId;
        public String groupName;
        public int subjectId;
        public String subjectName;

        public Pair(int groupingId, int groupId, String groupName, int subjectId, String subjectName) {
            this.groupingId = groupingId;
            this.groupId = groupId;
            this.groupName = groupName;
            this.subjectId = subjectId;
            this.subjectName = subjectName;
        }
    }

    private List<Pair> pairs = new ArrayList<>();

    public int getSelectedPair() {
        return selectedPair;
    }

    public void setSelectedPair(int selectedPair) {
        this.selectedPair = selectedPair;
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setGroupSubjectPairs(List<IGroupSubjectPair> pairList) {
        for (IGroupSubjectPair pair : pairList) {
            pairs.add(new Pair(pair.getGroupingId(), pair.getGroupId(), pair.getGroupName(), pair.getSubjectId(),
                    pair.getSubjectName()));
        }
        if (pairs.size() > 0 && selectedPair == 0) selectedPair = pairs.get(0).groupingId;
    }
}
