package rw.gov.sacco.stockmis.v1.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import rw.gov.sacco.stockmis.v1.security.UserPrincipal;
import rw.gov.sacco.stockmis.v1.models.User;

public class Mapper {

    public static ModelMapper modelMapper = new ModelMapper();
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static User getUserFromDTO(Object object, String password) {
        User user = getUserFromDTO(object);
        user.setPassword(passwordEncoder.encode(password));
        user.setId(null);
        return user;
    }

    public static String encode(String raw){
        return passwordEncoder.encode(raw);
    }

    public static boolean compare(String encoded, String raw){
        return passwordEncoder.matches(raw, encoded);
    }

    public static User getUserFromDTO(Object userPrincipal) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Specify a custom mapping rule to map the userName field of the UserPrincipal class to the setUserName method of the User class
        modelMapper.createTypeMap(UserPrincipal.class, User.class);

        return modelMapper.map(userPrincipal, User.class);
    }
}
