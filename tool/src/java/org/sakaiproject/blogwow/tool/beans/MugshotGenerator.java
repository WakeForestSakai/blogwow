package org.sakaiproject.blogwow.tool.beans;

import java.util.List;
import java.util.Random;

public class MugshotGenerator {
  private List<String> mugshotImages;
  public void setMugshotImages(List<String> images) {
      this.mugshotImages = images;
  }

  private Random generator = new Random();
  
  public String getMugshotUrl(String userid) {
      //int mugshot = userid.hashCode() % mugshotImages.size();
      return mugshotImages.get(generator.nextInt( mugshotImages.size() ));
      //return mugshotImages.get(mugshot);
  }
    
}
