package qa.dboyko.rococo.entity;

import com.dboyko.rococo.grpc.Userdata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"user\"")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column()
    private String firstname;

    @Column()
    private String lastname;

    @Column(name = "avatar", columnDefinition = "bytea")
    private byte[] avatar;

    public Userdata toUserdataGrpc() {
        return Userdata.newBuilder()
                .setUserId(this.getId().toString())
                .setUsername(this.getUsername())
                .setFirstname(this.getFirstname() != null ? this.getFirstname() : "")
                .setLastname(this.getLastname() != null ? this.getLastname() : "")
                .setAvatar(this.getAvatar() != null && this.getAvatar().length > 0 ? new String(this.getAvatar(), StandardCharsets.UTF_8) : "")
                .build();
    }

}
