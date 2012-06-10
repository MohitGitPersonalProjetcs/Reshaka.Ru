/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.TagManagerLocal;
import entity.Tag;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import web.utils.StringUtils;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class TagBean {

    @EJB
    TagManagerLocal tagMan;

    private Tag selectedTag;

    private List<Tag> selectedTagList;

    private String selectedText;

    public TagBean() {
        this.selectedTagList = new ArrayList();
        selectedText = "";
    }

    @PostConstruct
    private void init() {
        TagConverter.tagMan = this.tagMan;
    }

    public List<Tag> getSelectedTagList() {
        return selectedTagList;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public Tag getSelectedTag() {
        return selectedTag;
    }

    public void setSelectedTag(Tag selectedTag) {
        System.out.println("setSelectedTag  occured!!!");
        if (selectedTag == null) {
            System.out.println("selectedTag = null !!!!");
            return;
        }
        this.selectedTag = tagMan.addTag(selectedTag.getText());
        if (selectedTagList.contains(this.selectedTag) == false) {
            this.selectedTagList.add(this.selectedTag);
        }
    }

    public void addTag() {
        System.out.println("addTag occured! tag = " + selectedTag);
        if (selectedTagList.contains(this.selectedTag) == false) {
            this.selectedTagList.add(this.selectedTag);
        }
        // this.selectedTagList.add(selectedTag);
        System.out.println("selectedTagList = " + this.selectedTagList);
    }

    public List<String> completeTag(String query) {
        List<Tag> s = tagMan.completeTagList(StringUtils.getLastWord(query));
        List<String> list = new ArrayList();
        for (Tag tag : s) {
            if ("".equals(StringUtils.getFirstPartOfText(this.selectedText))) {
                list.add(tag.getText());
            } else {
                list.add(StringUtils.getFirstPartOfText(this.selectedText) + ", " + tag.getText());
            }
        }

        return list;
    }
}
