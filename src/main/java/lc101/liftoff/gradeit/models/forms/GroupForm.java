package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.Group;

import javax.validation.constraints.NotNull;

public class GroupForm {

    @NotNull(message = "Group Id can't be empty")
    private int groupId;

    private Iterable<Group> groups;

    public GroupForm() {
    }

    public GroupForm(int groupId, Iterable<Group> groups) {
        this.groupId = groupId;
        this.groups = groups;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Iterable<Group> getGroups() {
        return groups;
    }

    public void setGroups(Iterable<Group> groups) {
        this.groups = groups;
    }
}
