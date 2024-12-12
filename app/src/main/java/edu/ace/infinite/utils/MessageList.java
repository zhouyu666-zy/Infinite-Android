package edu.ace.infinite.utils;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.fragment.MessageFragment;

public class MessageList<T> extends ArrayList<T> {
    @Override
    public boolean add(T t) {
//        Hawk.put("messageList", MessageFragment.getMessageList());
        return super.add(t);
    }
}
