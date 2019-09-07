package com.entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class BaseEntry implements Serialize {

    @Id
    @Setter(AccessLevel.NONE)
    protected long id;

    public BaseEntry(long id) {
        this.id = id;
    }

}
