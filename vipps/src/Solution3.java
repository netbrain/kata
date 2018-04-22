import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Solution3 {

    private class Photo {
        String location;
        Date timestamp;
        String originalFilename;
        String newFilename;

        public Photo(String location, String originalFilename, Date timestamp) {
            this.location = location;
            this.originalFilename = originalFilename;
            this.timestamp = timestamp;
        }

        public void setNewFilename(String newFilename) {
            this.newFilename = newFilename;
        }

    }

    public String solution(String S){

        List<Photo> photos = new ArrayList<>();
        Map<String,Map<Date,Photo>> data = new HashMap<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (String line : S.split("\n")) {
            String[] columns = line.split(",");
            String filename = columns[0].trim();
            String location = columns[1].trim();
            Date timestamp = getDate(df, columns[2].trim());

            if (!data.containsKey(location)){
                data.put(location,new HashMap<>());
            }
            Photo photo = new Photo(location,filename,timestamp);
            photos.add(photo);
            data.get(location).put(timestamp,photo);
        }

        for (String locationKey : data.keySet()) {
            int index = 0;
            Map<Date, Photo> location = data.get(locationKey);
            Date[] dateKeys = location.keySet().toArray(new Date[]{});
            Arrays.sort(dateKeys);
            int leadingZeros = dateKeys.length/10+1;
            for (Date dateKey : dateKeys) {
                Photo photo = location.get(dateKey);
                photo.setNewFilename(String.format("%s%0"+(leadingZeros)+"d.%s",photo.location,++index,getExtension(photo.originalFilename)));
            }
        }

        return String.join("\n",photos.stream().map(photo -> photo.newFilename).collect(Collectors.toList()));
    }


    private Date getDate(SimpleDateFormat df, String date) {
        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date input in dataset");
        }
    }

    private String getExtension(String f){
        int i = f.lastIndexOf('.');
        if (i > 0) {
            return f.substring(i+1);
        }
        return "";
    }
}
