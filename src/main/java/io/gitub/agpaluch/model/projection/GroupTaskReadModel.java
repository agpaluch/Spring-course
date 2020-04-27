package io.gitub.agpaluch.model.projection;

import io.gitub.agpaluch.model.Task;

public class GroupTaskReadModel {
    private boolean done;
    private String description;

    GroupTaskReadModel(Task source) {
        description = source.getDescription();
        done = source.isDone();
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
