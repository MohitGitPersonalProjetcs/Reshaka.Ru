/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Tag;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface TagManagerLocal {
    
    public Tag addTag(String text);
    
    public void addTags(String query);
    
    public Tag getTagByText(String text);
    
    public boolean tagExixts(String text);
    
    public void deleteTag(Long tagId);
    
    public void deleteTag(String text);
    
    public List<Tag> completeTagList(String query);
    
}
