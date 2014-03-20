/*
 * Copyright 2014 bruce.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package classifier;

/**
 *
 * @author bruce
 */
public class BorrowListItem {

    public String userid;
    public String username;
    public String major;
    public String college;
    public String bookname;
    public String author;
    public String topic;
    public String lang;

    public boolean isValid() {

        if (userid == null) {
            return false;
        }
        if (username == null) {
            return false;
        }
        if (major == null) {
            return false;
        }
        if (college == null) {
            return false;
        }
        if (bookname == null) {
            return false;
        }
        if (author == null) {
            return false;
        }
        if (topic == null) {
            return false;
        }
        if (lang == null) {
            return false;
        }

        return true;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}
